package com.spring.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import com.spring.app.entity.BusinessConform;
import com.spring.app.entity.Draft;
import com.spring.app.entity.DraftFile;
import com.spring.app.entity.DraftLine;
import com.spring.app.entity.DraftType;
import com.spring.app.entity.Member;
import com.spring.app.entity.Payment;
import com.spring.app.entity.PaymentList;
import com.spring.app.entity.Signline;
import com.spring.app.entity.SignlineMember;
import com.spring.app.entity.Vacation;
import com.spring.app.model.BusinessConformRepository;
import com.spring.app.model.BusinessRepository;
import com.spring.app.model.DraftFileRepository;
import com.spring.app.model.DraftLineRepository;
import com.spring.app.model.DraftRepository;
import com.spring.app.model.MemberRepository;
import com.spring.app.model.PaymentListRepository;
import com.spring.app.model.PaymentRepository;
import com.spring.app.model.SignlineRepository;
import com.spring.app.model.VacationRepository;
import com.spring.app.service.MemberService;
import com.spring.app.service.SignService;
import com.spring.app.service.SignlineService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sign")
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
    private final DraftFileRepository draftFileRepository; // ★ 첨부

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager em;

    private final SignService signService;
    private final SignlineService signlineService;
    private final MemberService memberService;

    /* ===================== 메인/요약 ===================== */
    @GetMapping("main")
    public String signmain(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        ZoneId ZONE = ZoneId.of("Asia/Seoul");

        // 1) 결재 대기 3건
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

        // 2) 내가 상신한 문서 3건
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
            m.put("status", status);
            m.put("regdate", d.getDraftRegdate()==null ? null :
                    java.util.Date.from(d.getDraftRegdate().atZone(ZONE).toInstant()));
            sentPreview.add(m);
            if (sentPreview.size() == 3) break;
        }

        // 3) 내가 처리한 이력 3건
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

    /* ===================== 작성 화면 ===================== */
    @GetMapping("add")
    public String signadd(HttpSession session, Model model) {
        Long previewNo = draftRepository.peekNextDraftNo();
        model.addAttribute("previewNo", previewNo);

        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        String deptName = "";
        if (login != null) {
            if (login.getDepartment() != null && login.getDepartment().getDepartmentName() != null) {
                deptName = login.getDepartment().getDepartmentName();
            } else if (login.getFkDepartmentSeq() > 0) {
                try { deptName = memberRepository.findDeptName(login.getFkDepartmentSeq()); } catch (Exception ignore) {}
            }
        }
        model.addAttribute("loginDeptName", deptName);
        return "/sign/signadd";
    }

    /* ===================== 환경설정(도장) ===================== */
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
        if (!dir.exists()) dir.mkdirs();

        try {
            String filename = request.getHeader("file-name");
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
                try { fileManager.doFileDelete(savedFilename, path); } catch (Exception ignore) {}
            }
            memberRepository.clearStampImageByUserid(userid);
            out.print("{\"result\":\"success\"}");
        } catch (IOException e) {
            e.printStackTrace();
            try { response.getWriter().print("{\"result\":\"fail\"}"); } catch (IOException ignore) {}
        }
    }

    /* ===================== 결재라인 설정 ===================== */
    @GetMapping("setting/line")
    public String linePopup(@RequestParam(required = false) Long id, Model model) {
        if (id != null) model.addAttribute("signline", signlineService.getLinesWithMembers(id));
        return "sign/signlinepopup";
    }

    @PostMapping("lines/{id}/delete")
    @Transactional
    @ResponseBody
    public Map<String, Object> deleteLine(@PathVariable("id") Long id, HttpSession session) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) return Map.of("ok", false, "msg", "로그인이 필요합니다.");

        var opt = signlineRepository.findById(id);
        if (opt.isEmpty()) return Map.of("ok", false, "msg", "존재하지 않는 결재라인입니다.");

        var line = opt.get();
        if (!Objects.equals(line.getFkMemberSeq(), Long.valueOf(loginuser.getMemberSeq())))
            return Map.of("ok", false, "msg", "삭제 권한이 없습니다.");

        signlineRepository.delete(line);
        return Map.of("ok", true);
    }

    @GetMapping("members")
    @ResponseBody
    public List<MemberDTO> members(HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        int me = login.getMemberSeq();
        return memberService.getSignlineMember().stream()
                .filter(m -> m.getMemberSeq() != me)  // 본인 제외
                .toList();
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

        if ((approverSeqs == null || approverSeqs.isEmpty()) && approverSeqsAlt != null) {
            approverSeqs = approverSeqsAlt;
        }
        if (lineName == null || lineName.isBlank() || approverSeqs == null || approverSeqs.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign/setting");
            return;
        }

        if (id == null) {
            Signline line = Signline.builder()
                    .fkMemberSeq(Long.valueOf(loginuser.getMemberSeq()))
                    .signlineName(lineName)
                    .regdate(java.time.LocalDateTime.now())
                    .build();

            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .fkMemberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }
            signlineRepository.save(line);
        } else {
            Signline line = signlineRepository.findById(id).orElseThrow();
            line.setSignlineName(lineName);
            line.getMembers().clear();
            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .fkMemberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }
            signlineRepository.save(line);
        }

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
        return signlineService.findAllByOwner(loginuser.getMemberSeq());
    }

    @GetMapping("line/load")
    public String lineLoadPopup() {
        return "sign/signlineloadpopup";
    }

    @GetMapping("lines/{id}")
    @ResponseBody
    public Signline lineDetail(@PathVariable("id") Long id) {
        return signlineService.getLineWithMembers(id);
    }

    /* ===================== 목록(탭) ===================== */
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
            map.put("regdate", regDate);
            map.put("lineOrder", dl.getLineOrder());
            map.put("myStatus", dl.getSignStatus());
            list.add(map);
        }
        model.addAttribute("rows", list);
        return "sign/inbox";
    }

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

    @GetMapping("history")
    public String history(HttpSession session, Model model) {
        Long me = Long.valueOf(((MemberDTO)session.getAttribute("loginuser")).getMemberSeq());
        var rows = draftLineRepository.findHistory(me);

        ZoneId ZONE = ZoneId.of("Asia/Seoul");

        var list = new ArrayList<Map<String,Object>>();
        for (var dl : rows) {
            var d = dl.getDraft();

            Date regDate = (d.getDraftRegdate() == null) ? null
                    : Date.from(d.getDraftRegdate().atZone(ZONE).toInstant());

            Date signDate = (dl.getSignDate() == null) ? null
                    : Date.from(dl.getSignDate().atZone(ZONE).toInstant());

            var map = new LinkedHashMap<String,Object>();
            map.put("draftSeq", d.getDraftSeq());
            map.put("title", d.getDraftTitle());
            map.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            map.put("drafterName", d.getMember()!=null? d.getMember().getMemberName() : "-");
            map.put("isEmergency", d.getIsEmergency());
            map.put("regdate", regDate);
            map.put("lineOrder", dl.getLineOrder());
            map.put("myStatus", dl.getSignStatus());
            map.put("signDate", signDate);
            map.put("draftLineSeq", dl.getDraftLineSeq());
            list.add(map);
        }
        model.addAttribute("rows", list);
        return "sign/history";
    }

    /* ===================== 상신(4종) ===================== */
    @Transactional
    @PostMapping(value="/draft/proposal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String submitProposal(
        @RequestParam Integer fk_draft_type_seq, // 3
        @RequestParam Long fk_member_seq,
        @RequestParam(name="is_emergency", defaultValue="0") Integer isEmergency,
        @RequestParam String conform_title,
        @RequestParam String conform_content,
        @RequestPart(required=false) List<MultipartFile> files, // ★ 첨부
        @RequestParam List<Long> approverSeq,
        @RequestParam List<Integer> lineOrder,
        @RequestParam(required=false) String draft_title,
        @RequestParam(required=false) String draft_content,
        HttpServletRequest request,
        HttpSession session
    ){
        Draft draft = Draft.builder()
                .draftType(em.getReference(DraftType.class, fk_draft_type_seq.longValue()))
                .member(em.getReference(Member.class, fk_member_seq))
                .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : conform_title)
                .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : conform_content)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        draft = draftRepository.save(draft);

        businessConformRepository.save(
                BusinessConform.builder()
                        .draftSeq(draft.getDraftSeq())
                        .conformTitle(conform_title)
                        .conformContent(conform_content)
                        .build()
        );

        saveApprovalLine(draft, approverSeq, lineOrder);

        // ★ 첨부파일 저장
        saveDraftFiles(draft, files, request, session);

        return "redirect:/sign/main";
    }

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
        @RequestParam String vacation_type, // "ANNUAL" | "HALF"
        @RequestParam List<Long> approverSeq,
        @RequestParam List<Integer> lineOrder,
        @RequestParam(required=false) String draft_title,
        @RequestParam(required=false) String draft_content
    ){
        Draft draft = Draft.builder()
            .draftType(em.getReference(DraftType.class, fk_draft_type_seq.longValue()))
            .member(em.getReference(Member.class, fk_member_seq))
            .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : vacation_title)
            .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : vacation_content)
            .draftStatus(0)
            .isEmergency(isEmergency)
            .build();
        draft = draftRepository.save(draft);

        vacationRepository.save(
            Vacation.builder()
                .draftSeq(draft.getDraftSeq())
                .vacationTitle(vacation_title)
                .vacationType(vacation_type)
                .vacationStart(vacation_start)
                .vacationEnd(vacation_end)
                .vacationContent(vacation_content)
                .build()
        );

        saveApprovalLine(draft, approverSeq, lineOrder);
        return "redirect:/sign/main";
    }

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
        Draft draft = Draft.builder()
                .draftType(em.getReference(DraftType.class, fk_draft_type_seq.longValue()))
                .member(em.getReference(Member.class, fk_member_seq))
                .draftTitle((draft_title != null && !draft_title.isBlank()) ? draft_title : payment_title)
                .draftContent((draft_content != null && !draft_content.isBlank()) ? draft_content : payment_content)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        draft = draftRepository.save(draft);

        paymentRepository.save(
            Payment.builder()
                .draftSeq(draft.getDraftSeq())
                .paymentTitle(payment_title)
                .paymentContent(payment_content)
                .totalAmount(total_amount == null ? 0L : total_amount)
                .build()
        );

        int n = Math.max(payment_list_regdate.size(), Math.max(uses.size(), prices.size()));
        ArrayList<PaymentList> rows = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            LocalDate d = i < payment_list_regdate.size() ? payment_list_regdate.get(i) : null;
            String    u = i < uses.size() ? uses.get(i) : null;
            Long      p = i < prices.size() ? prices.get(i) : 0L;
            if (d == null && (u == null || u.isBlank()) && (p == null || p == 0L)) continue;
            rows.add(
                PaymentList.builder()
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
        Draft draft = Draft.builder()
                .draftType(em.getReference(DraftType.class, fk_draft_type_seq.longValue()))
                .member(em.getReference(Member.class, fk_member_seq))
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
        var lines = new ArrayList<DraftLine>();
        for (int i = 0; i < approverSeq.size(); i++) {
            Long approverId = approverSeq.get(i);
            Integer ord = (lineOrder != null && i < lineOrder.size() && lineOrder.get(i) != null)
                            ? lineOrder.get(i) : (i + 1);
            if (ord == null || ord < 1) ord = i + 1;

            lines.add(DraftLine.builder()
                    .draft(draft)
                    .approver(em.getReference(Member.class, approverId))
                    .lineOrder(ord)
                    .signStatus(0)
                    .build());
        }
        draftLineRepository.saveAll(lines);
    }

    /* ===================== 승인/반려 & 연차 반영 ===================== */
    @PostMapping("lines/{draftLineSeq}/approve")
    @Transactional
    @ResponseBody
    public Map<String,Object> approve(@PathVariable("draftLineSeq") Long draftLineSeq,
                                      @RequestParam(name="comment", required=false) String comment,
                                      HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());

        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, me).orElse(null);
        if (mine == null) return Map.of("ok", false, "msg", "내 결재선이 아닙니다.");

        Draft d = anyLine.getDraft();
        int prevStatus = (d.getDraftStatus()==null ? 0 : d.getDraftStatus());

        boolean editing = !Integer.valueOf(0).equals(mine.getSignStatus());
        if (!editing) {
            Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
            if (nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
                return Map.of("ok", false, "msg", "지금은 결재할 수 없는 상태입니다.");
            }
        }

        mine.setSignStatus(1);
        mine.setSignComment(comment);
        mine.setSignDate(java.time.LocalDateTime.now());
        draftLineRepository.save(mine);

        List<DraftLine> lines = draftLineRepository
            .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
        boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));

        d.setDraftStatus(anyReject ? 9 : (allApprove ? 1 : 0));
        draftRepository.save(d);

        if (prevStatus == 1 && !allApprove) {
            revertVacationDeductionIfNeeded(d);
        } else if (prevStatus != 1 && allApprove) {
            applyVacationDeductionIfNeeded(d);
        }

        return Map.of("ok", true, "lineSeq", mine.getDraftLineSeq());
    }

    private void applyVacationDeductionIfNeeded(Draft draft) {
        var optVac = vacationRepository.findByDraftSeq(draft.getDraftSeq());
        if (optVac.isEmpty()) return;
        var v = optVac.get();
        java.math.BigDecimal useDays = calcVacationDays(v);

        em.createNativeQuery(
            "UPDATE TBL_ANNUAL_LEAVE " +
            "   SET USED_LEAVE = USED_LEAVE + :d, " +
            "       REMAINING_LEAVE = REMAINING_LEAVE - :d " +
            " WHERE MEMBER_SEQ = :m"
        ).setParameter("d", useDays)
         .setParameter("m", draft.getMember().getMemberSeq())
         .executeUpdate();
    }

    private void revertVacationDeductionIfNeeded(Draft draft) {
        var optVac = vacationRepository.findByDraftSeq(draft.getDraftSeq());
        if (optVac.isEmpty()) return;
        var v = optVac.get();
        java.math.BigDecimal useDays = calcVacationDays(v);

        em.createNativeQuery(
            "UPDATE TBL_ANNUAL_LEAVE " +
            "   SET USED_LEAVE = USED_LEAVE - :d, " +
            "       REMAINING_LEAVE = REMAINING_LEAVE + :d " +
            " WHERE MEMBER_SEQ = :m"
        ).setParameter("d", useDays)
         .setParameter("m", draft.getMember().getMemberSeq())
         .executeUpdate();
    }

    private java.math.BigDecimal calcVacationDays(Vacation v) {
        if ("HALF".equalsIgnoreCase(v.getVacationType())) return new java.math.BigDecimal("0.5");
        long days = java.time.temporal.ChronoUnit.DAYS.between(v.getVacationStart(), v.getVacationEnd()) + 1;
        return new java.math.BigDecimal(days);
    }

    @PostMapping("lines/{draftLineSeq}/reject")
    @Transactional
    @ResponseBody
    public Map<String,Object> reject(@PathVariable("draftLineSeq") Long draftLineSeq,
                                     @RequestParam(name="comment") String comment,
                                     HttpSession session) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        Long me = Long.valueOf(login.getMemberSeq());

        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, me).orElse(null);
        if (mine == null) return Map.of("ok", false, "msg", "내 결재선이 아닙니다.");

        Draft d = anyLine.getDraft();
        int prevStatus = (d.getDraftStatus()==null ? 0 : d.getDraftStatus());

        boolean editing = !Integer.valueOf(0).equals(mine.getSignStatus());
        if (!editing) {
            Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
            if (nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
                return Map.of("ok", false, "msg", "지금은 결재할 수 없는 상태입니다.");
            }
        }

        mine.setSignStatus(9);
        mine.setSignComment(comment);
        mine.setSignDate(java.time.LocalDateTime.now());
        draftLineRepository.save(mine);

        List<DraftLine> lines = draftLineRepository
            .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
        boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));

        d.setDraftStatus(anyReject ? 9 : (allApprove ? 1 : 0));
        draftRepository.save(d);

        if (prevStatus == 1 && !allApprove) {
            revertVacationDeductionIfNeeded(d);
        }

        return Map.of("ok", true, "lineSeq", mine.getDraftLineSeq());
    }

    /* ===================== 상세 보기 ===================== */
    @GetMapping("/view/{draftSeq}")
    @Transactional(readOnly = true)
    public String view(@PathVariable("draftSeq") Long draftSeq, HttpSession session, Model model) {
        MemberDTO login = (MemberDTO) session.getAttribute("loginuser");
        if (login == null) throw new IllegalStateException("로그인 필요");

        Long me = Long.valueOf(login.getMemberSeq());

        // 1) 결재라인
        List<DraftLine> lines = draftLineRepository.findLinesWithApprover(draftSeq);
        if (lines.isEmpty()) throw new IllegalArgumentException("결재라인이 존재하지 않습니다. draftSeq=" + draftSeq);

        // 2) 본문
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

        // 2-1) 서브 엔티티 필요 시
        vacationRepository.findByDraftSeq(draftSeq).ifPresent(v -> model.addAttribute("vacation", v));

        // 3) 내 라인/다음 순번
        DraftLine myLine = draftLineRepository.findMyLine(draftSeq, me).orElse(null);
        Integer  nextOrd = draftLineRepository.findNextOrder(draftSeq);

        boolean canActNow = (myLine != null)
                && Integer.valueOf(0).equals(myLine.getSignStatus())
                && (nextOrd != null && myLine.getLineOrder().equals(nextOrd));
        boolean canEdit = (myLine != null);

        String docTypeName = (draft.getDraftType() != null && draft.getDraftType().getDraftTypeName() != null)
                ? draft.getDraftType().getDraftTypeName() : "문서";
        String myStamp = memberRepository.findStampImageByUserid(login.getMemberUserid());
        if (myStamp == null) myStamp = "";

        // 4) ★ 첨부 목록
        List<DraftFile> attachments = draftFileRepository
                .findByDraft_DraftSeqOrderByDraftFileSeqAsc(draftSeq);

        // 휴가처럼 지출도 모델에 태우기
        paymentRepository.findByDraftSeq(draftSeq).ifPresent(p -> {
            model.addAttribute("payment", p);
            List<PaymentList> items =
                paymentListRepository.findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(draftSeq);
            model.addAttribute("paymentLists", items);
        });
        
        model.addAttribute("draft", draft);
        model.addAttribute("lines", lines);
        model.addAttribute("canAct", canActNow);
        model.addAttribute("canEdit", canEdit);
        model.addAttribute("myDraftLineSeq", (myLine != null ? myLine.getDraftLineSeq() : null));
        model.addAttribute("loginMemberSeq", me);
        model.addAttribute("docTypeName", docTypeName);
        model.addAttribute("myStampImage", myStamp);
        model.addAttribute("attachments", attachments); // ★ 첨부

        return "sign/view";
    }

    /* ===================== 첨부 저장 & 다운로드 ===================== */
    private void saveDraftFiles(Draft draft, List<MultipartFile> files,
                                HttpServletRequest request, HttpSession session) {
        if (files == null || files.isEmpty()) return;

        String root = session.getServletContext().getRealPath("/");
        String path = root + "resources" + File.separator + "edoc_upload"; // URL: /resources/edoc_upload
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        List<DraftFile> list = new ArrayList<>();
        for (MultipartFile mf : files) {
            if (mf == null || mf.isEmpty()) continue;
            try {
                String original = mf.getOriginalFilename();
                byte[] bytes = mf.getBytes();
                String saved = fileManager.doFileUpload(bytes, original, path); // 새 파일명 생성/저장

                DraftFile df = DraftFile.builder()
                        .draft(draft)
                        .fileName(original)                                 // 화면 표시용
                        .filePath("/resources/edoc_upload/" + saved)         // 웹 경로
                        .build();
                list.add(df);
            } catch (Exception e) {
                e.printStackTrace(); // 문제 있는 파일만 건너뛰기
            }
        }
        if (!list.isEmpty()) draftFileRepository.saveAll(list);
    }

    @GetMapping("files/{id}/download")
    public void downloadFile(@PathVariable("id") Long id,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             HttpSession session) throws IOException {
        DraftFile f = draftFileRepository.findById(id).orElse(null);
        if (f == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // TODO: 접근 제어(기안자/결재선 등) 필요 시 여기서 검사

        String webPath = f.getFilePath(); // 예: /resources/edoc_upload/2025...uuid.ext
        String root = session.getServletContext().getRealPath("/");
        String real = webPath.startsWith("/") ? root + webPath.substring(1) : root + webPath;

        File file = new File(real);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/octet-stream");
        String encoded = URLEncoder.encode(f.getFileName(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
        response.setContentLengthLong(file.length());

        java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
    }
    
    @PostMapping("/downloadExcelFile")
    public String downloadExcel(@RequestParam Long draftSeq, Model model) {
        signService.exportDraftToExcel(draftSeq, model);
        return "excelDownloadView"; 
    }
    
}
