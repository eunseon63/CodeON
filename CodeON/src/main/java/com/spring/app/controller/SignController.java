package com.spring.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.FileManager;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.SignlineDTO;
import com.spring.app.entity.Business;
import com.spring.app.entity.Draft;
import com.spring.app.entity.DraftLine;
import com.spring.app.entity.DraftType;
import com.spring.app.entity.Member;
import com.spring.app.entity.Signline;
import com.spring.app.entity.SignlineMember;
import com.spring.app.entity.Vacation;
import com.spring.app.model.BusinessConformRepository;
import com.spring.app.model.BusinessRepository;
import com.spring.app.model.DraftLineRepository;
import com.spring.app.model.DraftRepository;
import com.spring.app.model.MemberRepository;
import com.spring.app.model.PaymentListRepository;
import com.spring.app.model.PaymentRepository;
import com.spring.app.model.SignlineRepository;
import com.spring.app.model.VacationRepository;
import com.spring.app.service.MemberService;
import com.spring.app.service.SignlineService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sign")   // ★ 중요: /sign/* -> /sign 로 변경
public class SignController {

    private final FileManager fileManager;
    private final MemberRepository memberRepository;
    private final SignlineRepository signlineRepository;
    private final DraftRepository draftRepository;
    private final DraftLineRepository draftLineRepository;
    private final VacationRepository vacationRepository;
    private final BusinessRepository businessRepository;
    private final BusinessConformRepository businessConformRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentListRepository paymentListRepository;
    
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager em;
    
    private final SignlineService signlineService;
    private final MemberService memberService;

    @GetMapping("main")
    public String signmain(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        ZoneId ZONE = ZoneId.of("Asia/Seoul");

        // 1) 결재 대기 문서 (내가 결재해야 할 것) → inbox
        var inboxRows = draftLineRepository.findInbox(me);
        var inboxPreview = new ArrayList<Map<String,Object>>();
        for (var dl : inboxRows) {
            var d = dl.getDraft();
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("isEmergency", d.getIsEmergency());
            m.put("regdate", d.getDraftRegdate()==null ? null :
                    java.util.Date.from(d.getDraftRegdate().atZone(ZONE).toInstant()));
            inboxPreview.add(m);
            if (inboxPreview.size() == 3) break;
        }

        // 2) 결재 진행 문서 (내가 상신한 문서) → sent
        var myDrafts = draftRepository.findByMember_MemberSeqOrderByDraftSeqDesc(me);
        var sentPreview = new ArrayList<Map<String,Object>>();
        for (var d : myDrafts) {
            var lines = draftLineRepository
                .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(d.getDraftSeq());
            boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
            boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));
            int status = anyReject ? 9 : (allApprove ? 1 : 0);

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("status", status); // 0:진행중,1:완료,9:반려
            m.put("regdate", d.getDraftRegdate()==null ? null :
                    java.util.Date.from(d.getDraftRegdate().atZone(ZONE).toInstant()));
            sentPreview.add(m);
            if (sentPreview.size() == 3) break;
        }

        // 3) 결재 완료 문서 (내가 처리한 이력) → history
        var historyRows = draftLineRepository.findHistory(me);
        var historyPreview = new ArrayList<Map<String,Object>>();
        for (var dl : historyRows) {
            var d = dl.getDraft();
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("signDate", dl.getSignDate()==null ? null :
                    java.util.Date.from(dl.getSignDate().atZone(ZONE).toInstant()));
            historyPreview.add(m);
            if (historyPreview.size() == 3) break;
        }

        model.addAttribute("inboxPreview", inboxPreview);
        model.addAttribute("sentPreview", sentPreview);
        model.addAttribute("historyPreview", historyPreview);
        return "/sign/signmain";
    }

    
    @GetMapping("add")
    public String signadd(HttpServletRequest request) {

    	Long previewNo = draftRepository.peekNextDraftNo(); // 미리보기용
    	request.setAttribute("previewNo", previewNo);
    	
        return "/sign/signadd";
    }

    @GetMapping("setting")
    public String signsetting(HttpSession session, HttpServletRequest request, Model model) {

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser != null) {
            String savedFilename = memberRepository.findStampImageByUserid(loginuser.getMemberUserid());
            if (savedFilename != null && !savedFilename.isBlank()) {
                String ctxPath = request.getContextPath();
                String stampUrl = ctxPath + "/resources/stamp_upload/" + savedFilename;

                model.addAttribute("stampFilename", savedFilename);
                model.addAttribute("stampUrl", stampUrl);
            }
        }

        return "/sign/signsetting";
    }

    @PostMapping("stampImageSave")
    public void stampImageSave(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String root = session.getServletContext().getRealPath("/");
        String path = root + "resources" + File.separator + "stamp_upload";

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            String filename = request.getHeader("file-name"); // 원본 파일명
            InputStream is = request.getInputStream();

            String newFilename = fileManager.doFileUpload(is, filename, path);

            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
            memberRepository.stampImageSave(loginuser.getMemberUserid(), newFilename);

            String ctxPath = request.getContextPath();
            String fileUrl = ctxPath + "/resources/stamp_upload/" + newFilename;

            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"result\":\"success\", \"url\":\"" + fileUrl + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"result\":\"fail\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @PostMapping("stampImageDelete")
    @ResponseBody
    @Transactional
    public void stampImageDelete(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            if (loginuser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"result\":\"fail\",\"reason\":\"unauth\"}");
                return;
            }

            String userid = loginuser.getMemberUserid();

            String savedFilename = memberRepository.findStampImageByUserid(userid);

            if (savedFilename != null && !savedFilename.isBlank()) {
                String root = session.getServletContext().getRealPath("/");
                String path = root + "resources" + File.separator + "stamp_upload";
                try {
                    fileManager.doFileDelete(savedFilename, path);
                } catch (Exception ignore) {}
            }

            memberRepository.clearStampImageByUserid(userid);

            out.print("{\"result\":\"success\"}");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.getWriter().print("{\"result\":\"fail\"}");
            } catch (IOException ignore) {}
        }
    }

    // 결재라인 팝업 (id 있으면 상세 조회해서 모델에 담음)
    @GetMapping("setting/line")
    public String linePopup(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("signline", signlineService.getLinesWithMembers(id));
        }
        return "sign/signlinepopup";
    }

    // 좌측 목록: 부서/직원 리스트
    @GetMapping("members")
    @ResponseBody
    public List<MemberDTO> members() {
        return memberService.getSignlineMember();
    }
    
    @PostMapping("lines/write")
    @Transactional
    public void saveLine(
            @RequestParam String lineName,
            @RequestParam(value="approverSeq",  required=false) List<Long> approverSeqs,
            @RequestParam(value="approverSeq[]",required=false) List<Long> approverSeqsAlt,
            @RequestParam(value="id", required=false) Long id,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

    	MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            response.sendRedirect(request.getContextPath() + "/login/loginStart");
            return;
        }

        // 1) 파라미터 합치기 (approverSeq 또는 approverSeq[])
        if ((approverSeqs == null || approverSeqs.isEmpty()) && approverSeqsAlt != null) {
            approverSeqs = approverSeqsAlt;
        }
        if (lineName == null || lineName.isBlank() || approverSeqs == null || approverSeqs.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign/setting");
            return;
        }

        // 2) 신규/수정
        if (id == null) {
            // 신규
            Signline line = Signline.builder()
                    .fkMemberSeq(Long.valueOf(loginuser.getMemberSeq()))
                    .signlineName(lineName)
                    .regdate(java.time.LocalDateTime.now())
                    .build();

            int order = 1;
            for (Long mseq : approverSeqs) {
                // 자식에 부모 세팅 (addMember가 내부에서 setSignline(this) 하도록 구현되어 있어야 함)
                line.addMember(SignlineMember.builder()
                        .fkMemberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }

            signlineRepository.save(line); // cascade=ALL 이면 자식까지 insert
        } else {
            // 수정
            Signline line = signlineRepository.findById(id).orElseThrow();
            line.setSignlineName(lineName);

            // 기존 멤버 제거 (orphanRemoval=true 필요)
            line.getMembers().clear();

            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .fkMemberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }
            // save 생략 가능(더티 체킹), 있어도 무방
            signlineRepository.save(line);
        }

        // 3) 응답 (부모 새로고침)
        String ctx = request.getContextPath();
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><body><script>");
            out.println("if (window.opener && !window.opener.closed) {");
            out.println("  try { if (window.opener.loadSavedLines) window.opener.loadSavedLines(); } catch(e) {}");
            out.println("  window.close();");
            out.println("} else {");
            out.println("  window.location.replace('" + ctx + "/sign/setting');");
            out.println("}");
            out.println("</script></body></html>");
        }
    }

    @GetMapping("lines")
    @ResponseBody
    public List<SignlineDTO> lines(HttpSession session) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        // 로그인 사용자의 결재라인 목록 반환
        return signlineService.findAllByOwner(loginuser.getMemberSeq());
    }
    
    @GetMapping("line/load")
    public String lineLoadPopup() {
        return "sign/signlineloadpopup";   // /WEB-INF/views/sign/signlineloadpopup.jsp
    }
    
    @GetMapping("lines/{id}")
    @ResponseBody
    public Signline lineDetail(@PathVariable Long id) {
        return signlineService.getLineWithMembers(id);
    }
    

    /* ===== Console helpers ===== */
    private void pout(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    private void dumpParams(HttpServletRequest req) {
        req.getParameterMap().forEach((k, v) ->
            System.out.println(String.format("[REQ] %s = %s", k, Arrays.toString(v)))
        );
    }

    private void dumpApprovalLine(List<Long> approverSeq, List<Integer> lineOrder) {
        if (approverSeq == null || approverSeq.isEmpty()) {
            System.out.println("[APPR] (empty)");
            return;
        }
        for (int i = 0; i < approverSeq.size(); i++) {
            Long seq = approverSeq.get(i);
            Integer ord = (lineOrder != null && i < lineOrder.size()) ? lineOrder.get(i) : (i + 1);
            System.out.println(String.format("[APPR] idx=%d memberSeq=%s lineOrder=%s", i, String.valueOf(seq), String.valueOf(ord)));
        }
    }

    // =============== 품의(업무품의서) ===============
    @Transactional
    @PostMapping(value="/draft/proposal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String submitProposal(
        @RequestParam Integer fk_draft_type_seq, // 3
        @RequestParam Long fk_member_seq,
        @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
        @RequestParam String conform_title,
        @RequestParam String conform_content,
        @RequestPart(required=false) List<MultipartFile> files,
        @RequestParam List<Long> approverSeq,
        @RequestParam List<Integer> lineOrder,
        @RequestParam(required=false) String draft_title,
        @RequestParam(required=false) String draft_content,
        HttpServletRequest request
    ){
        // 1) TBL_DRAFT
        com.spring.app.entity.Draft draft = com.spring.app.entity.Draft.builder()
                .draftType(em.getReference(com.spring.app.entity.DraftType.class, fk_draft_type_seq.longValue()))
                .member(em.getReference(com.spring.app.entity.Member.class, fk_member_seq))
                .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : conform_title)
                .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : conform_content)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        draft = draftRepository.save(draft);

        // 2) TBL_BUSINESS_CONFORM
        businessConformRepository.save(
                com.spring.app.entity.BusinessConform.builder()
                        .draftSeq(draft.getDraftSeq())
                        .conformTitle(conform_title)
                        .conformContent(conform_content)
                        .build()
        );

        // 3) 결재라인
        saveApprovalLine(draft, approverSeq, lineOrder);

        // (첨부파일 저장 필요하면 여기서 처리)

        return "redirect:/sign/main";
    }


    // =============== 휴가 ===============
    @Transactional
    @PostMapping("/draft/vacation")
    public String submitVacation(
        @RequestParam Integer fk_draft_type_seq,
        @RequestParam Long fk_member_seq,
        @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
        @RequestParam String vacation_title,
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate vacation_start,
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate vacation_end,
        @RequestParam String vacation_content,
        @RequestParam String vacation_type, // ★ "ANNUAL" | "HALF"
        @RequestParam List<Long> approverSeq,
        @RequestParam List<Integer> lineOrder,
        @RequestParam(required=false) String draft_title,
        @RequestParam(required=false) String draft_content
    ){
        // 1) DRAFT 저장
        Draft draft = Draft.builder()
            .draftType(em.getReference(DraftType.class, fk_draft_type_seq.longValue()))
            .member(em.getReference(Member.class, fk_member_seq))
            .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : vacation_title)
            .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : vacation_content)
            .draftStatus(0)
            .isEmergency(isEmergency)
            .build();
        draft = draftRepository.save(draft);

        // 2) VACATION 저장 (종류는 그대로 저장)
        vacationRepository.save(
            Vacation.builder()
                .draftSeq(draft.getDraftSeq())
                .vacationTitle(vacation_title)
                .vacationType(vacation_type)    // ★ "ANNUAL" 또는 "HALF"
                .vacationStart(vacation_start)
                .vacationEnd(vacation_end)
                .vacationContent(vacation_content)
                .build()
        );

        // 3) 결재라인 저장
        saveApprovalLine(draft, approverSeq, lineOrder);

        // 4) 연차 차감 (원하면 적용)
        //    HALF = 0.5일, ANNUAL = (종일) 종료일 포함 일수
        //    (남은/사용 칼럼이 소수 지원(NUMBER(12,1) 등)이어야 함)
        java.math.BigDecimal useDays =
            "HALF".equals(vacation_type)
                ? new java.math.BigDecimal("0.5")
                : new java.math.BigDecimal(java.time.temporal.ChronoUnit.DAYS.between(vacation_start, vacation_end) + 1);

        // 필요 시 사용 (소수 허용 컬럼이어야 함)
        // em.createNativeQuery(
        //     "UPDATE TBL_ANNUAL_LEAVE " +
        //     "   SET USED_LEAVE = USED_LEAVE + :d, " +
        //     "       REMAINING_LEAVE = REMAINING_LEAVE - :d " +
        //     " WHERE MEMBER_SEQ = :m")
        //     .setParameter("d", useDays)
        //     .setParameter("m", fk_member_seq)
        //     .executeUpdate();

        return "redirect:/sign/main";
    }

    // =============== 지출 ===============
    @Transactional
    @PostMapping("/draft/expense")
    public String submitExpense(
        @RequestParam Integer fk_draft_type_seq, // 4
        @RequestParam Long fk_member_seq,
        @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
        @RequestParam String payment_title,
        @RequestParam String payment_content,
        @RequestParam("payment_list_regdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> payment_list_regdate,
        @RequestParam(name="payment_list_content") List<String> uses,
        @RequestParam(name="payment_list_price[]") List<Long> prices,
        @RequestParam(defaultValue="0") Long total_amount,
        @RequestParam List<Long> approverSeq,
        @RequestParam List<Integer> lineOrder,
        @RequestParam(required=false) String draft_title,
        @RequestParam(required=false) String draft_content,
        HttpServletRequest request
    ){
        com.spring.app.entity.Draft draft = com.spring.app.entity.Draft.builder()
                .draftType(em.getReference(com.spring.app.entity.DraftType.class, fk_draft_type_seq.longValue()))
                .member(em.getReference(com.spring.app.entity.Member.class, fk_member_seq))
                .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : payment_title)
                .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : payment_content)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        draft = draftRepository.save(draft);

        // 헤더
        paymentRepository.save(
            com.spring.app.entity.Payment.builder()
                .draftSeq(draft.getDraftSeq())
                .paymentTitle(payment_title)
                .paymentContent(payment_content)
                .totalAmount(total_amount == null ? 0L : total_amount)
                .build()
        );

        // 내역 N건
        int n = Math.max(payment_list_regdate.size(), Math.max(uses.size(), prices.size()));
        java.util.ArrayList<com.spring.app.entity.PaymentList> rows = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            LocalDate d = i < payment_list_regdate.size() ? payment_list_regdate.get(i) : null;
            String    u = i < uses.size() ? uses.get(i) : null;
            Long      p = i < prices.size() ? prices.get(i) : 0L;

            if (d == null && (u == null || u.isBlank()) && (p == null || p == 0L)) continue;

            rows.add(
                com.spring.app.entity.PaymentList.builder()
                    .fkDraftSeq(draft.getDraftSeq())
                    .regdate(d == null ? LocalDate.now() : d)
                    .content(u)
                    .price(p == null ? 0L : p)
                    .build()
            );
        }
        if (!rows.isEmpty()) paymentListRepository.saveAll(rows);

        saveApprovalLine(draft, approverSeq, lineOrder);

        return "redirect:/sign/main";
    }


    // =============== 출장 ===============
    @Transactional
    @PostMapping("/draft/trip")
    public String submitTrip(
        @RequestParam Integer fk_draft_type_seq, // 2
        @RequestParam Long fk_member_seq,
        @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
        @RequestParam String business_title,
        @RequestParam String business_content,
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate business_start,
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate business_end,
        @RequestParam String business_location,
        @RequestParam String business_result,
        @RequestParam List<Long> approverSeq,
        @RequestParam List<Integer> lineOrder,
        @RequestParam(required=false) String draft_title,
        @RequestParam(required=false) String draft_content,
        HttpServletRequest request
    ){
        com.spring.app.entity.Draft draft = com.spring.app.entity.Draft.builder()
                .draftType(em.getReference(com.spring.app.entity.DraftType.class, fk_draft_type_seq.longValue()))
                .member(em.getReference(com.spring.app.entity.Member.class, fk_member_seq))
                .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : business_title)
                .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : business_result)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        draft = draftRepository.save(draft);

        businessRepository.save(
            Business.builder()
                .draftSeq(draft.getDraftSeq())
                .businessTitle(business_title)
                .businessContent(business_content)
                .businessStart(business_start)
                .businessEnd(business_end)
                .businessLocation(business_location)
                .businessResult(business_result)
                .build()
        );

        saveApprovalLine(draft, approverSeq, lineOrder);

        return "redirect:/sign/main";
    }

    
    private void saveApprovalLine(Draft draft, List<Long> approverSeq, List<Integer> lineOrder) {

        if (approverSeq == null || approverSeq.isEmpty()) {
            throw new IllegalArgumentException("결재라인이 비어 있습니다.");
        }

        var lines = new java.util.ArrayList<DraftLine>();

        for (int i = 0; i < approverSeq.size(); i++) {
            Long approverId = approverSeq.get(i);

            Integer ord = (lineOrder != null && i < lineOrder.size() && lineOrder.get(i) != null)
                            ? lineOrder.get(i)
                            : (i + 1); // ★ 폼이 안 보내도 보정 (NULL 방지)

            // 혹시라도 0 또는 음수 들어오면 막기
            if (ord == null || ord < 1) ord = i + 1;

            DraftLine dl = DraftLine.builder()
                    .draft(draft)
                    .approver(em.getReference(Member.class, approverId))
                    .lineOrder(ord)       // ★ 필수
                    .signStatus(0)        // 대기
                    .build();

            lines.add(dl);
        }

        draftLineRepository.saveAll(lines);
    }

 // 목록 1) 결재하기
    @GetMapping("inbox")
    public String inbox(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        var rows = draftLineRepository.findInbox(me);

        var list = new ArrayList<Map<String,Object>>();
        for (var dl : rows) {
            var d = dl.getDraft();
            Date regDate = d.getDraftRegdate()==null ? null
                : Date.from(d.getDraftRegdate().atZone(ZoneId.systemDefault()).toInstant());

            var map = new LinkedHashMap<String,Object>();
            map.put("draftSeq", d.getDraftSeq());
            map.put("draftLineSeq", dl.getDraftLineSeq());
            map.put("title", d.getDraftTitle());
            map.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            map.put("drafterName", d.getMember()!=null? d.getMember().getMemberName() : "-");
            map.put("isEmergency", d.getIsEmergency());
            map.put("regdate", regDate);      // ★ Date로
            map.put("lineOrder", dl.getLineOrder());
            map.put("myStatus", dl.getSignStatus());
            list.add(map);
        }
        model.addAttribute("rows", list);
        return "sign/inbox";
    }


 // 목록 2) 문서함(내가 상신한 문서)
    @GetMapping("sent")
    public String sent(HttpSession session, Model model){
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());

        List<Draft> drafts = draftRepository.findByMemberWithType(me);

        List<Map<String,Object>> rows = new ArrayList<>();
        for (Draft d : drafts) {
            // 결재 상태 계산
            List<DraftLine> lines = draftLineRepository
                .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(d.getDraftSeq());
            boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
            boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));
            int status = anyReject ? 9 : (allApprove ? 1 : 0);

            // 날짜 변환
            Date regDate = (d.getDraftRegdate()==null) ? null
                : Date.from(d.getDraftRegdate().atZone(ZoneId.of("Asia/Seoul")).toInstant());


            String docType = (d.getDraftType()!=null && d.getDraftType().getDraftTypeName()!=null)
                    ? d.getDraftType().getDraftTypeName() : "-";
            Integer isEmergency = (d.getIsEmergency()==null) ? 0 : d.getIsEmergency();

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title",    d.getDraftTitle());
            m.put("status",   status);
            m.put("regdate",  regDate);
            m.put("docType",  docType);       
            m.put("isEmergency", isEmergency); 
            rows.add(m);
        }

        model.addAttribute("rows", rows);
        return "/sign/sent";
    }

 // 목록 3) 결재함(내가 처리한 이력)
    @GetMapping("history")
    public String history(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        var rows = draftLineRepository.findHistory(me);

        ZoneId ZONE = ZoneId.of("Asia/Seoul"); // 운영/개발 동일 타임존 보장

        var list = new ArrayList<Map<String,Object>>();
        for (var dl : rows) {
            var d = dl.getDraft();

            // LocalDateTime -> Date (null-safe)
            Date regDate = (d.getDraftRegdate() == null) ? null
                    : Date.from(d.getDraftRegdate().atZone(ZONE).toInstant());

            Date signDate = (dl.getSignDate() == null) ? null
                    : Date.from(dl.getSignDate().atZone(ZONE).toInstant()); //  핵심 변경

            var map = new LinkedHashMap<String,Object>();
            map.put("draftSeq", d.getDraftSeq());
            map.put("title", d.getDraftTitle());
            map.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            map.put("drafterName", d.getMember()!=null? d.getMember().getMemberName() : "-");
            map.put("isEmergency", d.getIsEmergency());
            map.put("regdate", regDate);     //  Date로 넘김 → JSP에서 <fmt:formatDate> 가능
            map.put("lineOrder", dl.getLineOrder());
            map.put("myStatus", dl.getSignStatus());
            map.put("signDate", signDate);   // Date로 넘김 → JSP에서 <fmt:formatDate> 가능
            map.put("draftLineSeq", dl.getDraftLineSeq());
            list.add(map);
        }
        model.addAttribute("rows", list);
        return "sign/history";
    }

 // 승인
    @PostMapping("lines/{draftLineSeq}/approve")
    @Transactional
    @ResponseBody
    public Map<String,Object> approve(@PathVariable Long draftLineSeq,
                                      @RequestParam(required=false) String comment,
                                      HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());

        // 1) 라인/문서 식별
        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        // 2) 내 결재선 검증 + 순번 체크
        DraftLine mine = draftLineRepository.findMyLine(draftSeq, me).orElse(null);
        if (mine == null) return Map.of("ok", false, "msg", "내 결재선이 아닙니다.");

        Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
        if (!Integer.valueOf(0).equals(mine.getSignStatus()) || nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
            return Map.of("ok", false, "msg", "지금은 결재할 수 없는 상태입니다.");
        }

        // 3) 내 라인 승인
        mine.setSignStatus(1);
        mine.setSignComment(comment);
        mine.setSignDate(java.time.LocalDateTime.now());
        draftLineRepository.save(mine);

        // 4) 전체 상태 재계산
        List<DraftLine> lines = draftLineRepository
            .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
        boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));

        Draft d = anyLine.getDraft();
        d.setDraftStatus(anyReject ? 9 : (allApprove ? 1 : 0));
        draftRepository.save(d);

        // 5) ✅ 모두 승인된 경우에만 휴가 차감 시도
        if (allApprove) {
            applyVacationDeductionIfNeeded(d);
        }

        return Map.of("ok", true, "lineSeq", mine.getDraftLineSeq());
    }

    /** 최종 승인된 휴가 문서면 연차 차감 */
    private void applyVacationDeductionIfNeeded(Draft draft) {
        // 휴가 문서 여부: draftSeq로 Vacation 존재 확인
        var optVac = vacationRepository.findByDraftSeq(draft.getDraftSeq());
        if (optVac.isEmpty()) return; // 휴가 문서가 아니면 종료

        var v = optVac.get();

        // 사용 일수 계산 (HALF=0.5, 그 외: 시작~종료 '포함' 일수)
        java.math.BigDecimal useDays = calcVacationDays(v);

        // 차감: 제출자 기준으로 잔여/사용 연차 업데이트
        em.createNativeQuery(
            "UPDATE TBL_ANNUAL_LEAVE " +
            "   SET USED_LEAVE = USED_LEAVE + :d, " +
            "       REMAINING_LEAVE = REMAINING_LEAVE - :d " +
            " WHERE MEMBER_SEQ = :m"
        ).setParameter("d", useDays)
         .setParameter("m", draft.getMember().getMemberSeq())
         .executeUpdate();

        // (선택) 중복 차감 방지 플래그/로그가 있다면 여기서 표시/기록
    }

    /** HALF=0.5, 그 외는 (end-start)+1 */
    private java.math.BigDecimal calcVacationDays(com.spring.app.entity.Vacation v) {
        if ("HALF".equalsIgnoreCase(v.getVacationType())) {
            return new java.math.BigDecimal("0.5");
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(v.getVacationStart(), v.getVacationEnd()) + 1;
        return new java.math.BigDecimal(days);
    }


    // 반려
    @PostMapping("lines/{draftLineSeq}/reject")
    @Transactional
    @ResponseBody
    public Map<String,Object> reject(@PathVariable Long draftLineSeq,
                                     @RequestParam String comment,
                                     HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());

        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, me).orElse(null);
        if (mine == null) {
            return Map.of("ok", false, "msg", "내 결재선이 아닙니다.");
        }

        // 내 순번/대기 상태일 때만 반려 허용(원하면 순번 체크는 생략 가능)
        Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
        if (!Integer.valueOf(0).equals(mine.getSignStatus()) || nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
            return Map.of("ok", false, "msg", "지금은 결재할 수 없는 상태입니다.");
        }

        mine.setSignStatus(9);
        mine.setSignComment(comment);
        mine.setSignDate(java.time.LocalDateTime.now());
        draftLineRepository.save(mine);

        // 문서 상태 즉시 반려 처리
        Draft d = anyLine.getDraft();
        d.setDraftStatus(9);
        draftRepository.save(d);

        return Map.of("ok", true, "lineSeq", mine.getDraftLineSeq());
    }


    
    
    @GetMapping("/view/{draftSeq}")
    @Transactional(readOnly = true)
    public String view(@PathVariable Long draftSeq, HttpSession session, Model model) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) throw new IllegalStateException("로그인 필요");

        // 레포 쿼리들이 Long 파라미터라서 타입 맞춤
        Long me = Long.valueOf(login.getMemberSeq());

        // 1) 결재라인(+결재자/부서/직급) : 레포 메서드 사용
        List<DraftLine> lines = draftLineRepository.findLinesWithApprover(draftSeq);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("결재라인이 존재하지 않습니다. draftSeq=" + draftSeq);
        }

        // 2) Draft 본문은 LAZY 터지지 않도록 fetch join으로 한 번 더 가져옴
        //    (d.member, d.member.department/grade, d.draftType까지 초기화)
        Draft draft = em.createQuery("""
            select d
              from Draft d
              join fetch d.member m
              left join fetch m.department
              left join fetch m.grade
              left join fetch d.draftType
             where d.draftSeq = :id
            """, Draft.class)
            .setParameter("id", draftSeq)
            .getSingleResult();

        // 3) 내 라인 / 다음 결재순번 : 레포 메서드로 계산
        DraftLine myLine = draftLineRepository.findMyLine(draftSeq, me).orElse(null);
        Integer  nextOrd = draftLineRepository.findNextOrder(draftSeq); // 승인(1) 아닌 라인들 중 최소 순번

        // 권한: 내가 존재 + 내 상태=대기(0) + 내 순번이 nextOrd
        boolean canAct = (myLine != null)
                && Integer.valueOf(0).equals(myLine.getSignStatus())
                && (nextOrd != null && myLine.getLineOrder().equals(nextOrd));

        // 4) 뷰에 필요한 부가값
        String docTypeName = (draft.getDraftType() != null && draft.getDraftType().getDraftTypeName() != null)
                ? draft.getDraftType().getDraftTypeName()
                : "문서";

        String myStamp = memberRepository.findStampImageByUserid(login.getMemberUserid());
        if (myStamp == null) myStamp = "";

        // 5) 모델 바인딩
        model.addAttribute("draft", draft);
        model.addAttribute("lines", lines);
        model.addAttribute("canAct", canAct);
        model.addAttribute("myDraftLineSeq", (myLine != null ? myLine.getDraftLineSeq() : null));
        model.addAttribute("loginMemberSeq", me);
        model.addAttribute("docTypeName", docTypeName);
        model.addAttribute("myStampImage", myStamp);

        return "sign/view";
    }


    
}
