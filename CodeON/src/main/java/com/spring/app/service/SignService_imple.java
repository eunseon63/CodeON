package com.spring.app.service;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.chatting.controller.WebsocketEchoHandler;
import com.spring.app.common.FileManager;
import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.*;
import com.spring.app.model.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SignService_imple implements SignService {

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
    private final DraftFileRepository draftFileRepository;

    private final WebsocketEchoHandler wsHandler;

    @PersistenceContext
    private EntityManager em;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    /* ===================== Draft/라인/첨부 ===================== */
    @Override
    public Draft createDraft(int draftTypeSeq, long memberSeq, String title, String content, int isEmergency) {
        Draft d = Draft.builder()
                .draftType(em.getReference(DraftType.class, (long)draftTypeSeq))
                .member(em.getReference(Member.class, memberSeq))
                .draftTitle(title)
                .draftContent(content)
                .draftStatus(0)
                .isEmergency(isEmergency)
                .build();
        return draftRepository.save(d);
    }

    @Override
    public void saveApprovalLine(Draft draft, List<Long> approverSeq, List<Integer> lineOrder) {
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

    @Override
    public void saveDraftFiles(Draft draft, List<MultipartFile> files, String realUploadDir, String webBasePath) {
        if (files == null || files.isEmpty()) return;

        File dir = new File(realUploadDir);
        if (!dir.exists()) dir.mkdirs();

        List<DraftFile> list = new ArrayList<>();
        for (MultipartFile mf : files) {
            if (mf == null || mf.isEmpty()) continue;
            try {
                String original = mf.getOriginalFilename();
                String saved = fileManager.doFileUpload(mf.getBytes(), original, realUploadDir);
                DraftFile df = DraftFile.builder()
                        .draft(draft)
                        .fileName(original)
                        .filePath(ensureTrailingSlash(webBasePath) + saved)
                        .build();
                list.add(df);
            } catch (Exception ignore) {}
        }
        if (!list.isEmpty()) draftFileRepository.saveAll(list);
    }

    private static String ensureTrailingSlash(String p) {
        if (p == null || p.isBlank()) return "/";
        return p.endsWith("/") ? p : (p + "/");
    }

    /* ===================== 서브엔티티 저장 ===================== */
    @Override
    public void saveBusinessConform(long draftSeq, String title, String content) {
        businessConformRepository.save(
                BusinessConform.builder()
                        .draftSeq(draftSeq)
                        .conformTitle(title)
                        .conformContent(content)
                        .build()
        );
    }

    @Override
    public void saveVacation(long draftSeq, LocalDate start, LocalDate end, String type, String title, String content) {
        vacationRepository.save(
                Vacation.builder()
                        .draftSeq(draftSeq)
                        .vacationTitle(title)
                        .vacationType(type)
                        .vacationStart(start)
                        .vacationEnd(end)
                        .vacationContent(content)
                        .build()
        );
    }

    @Override
    public void savePayment(long draftSeq, String title, String content, long totalAmount,
                            List<LocalDate> dates, List<String> uses, List<Long> prices) {
        paymentRepository.save(
                Payment.builder()
                        .draftSeq(draftSeq)
                        .paymentTitle(title)
                        .paymentContent(content)
                        .totalAmount(totalAmount)
                        .build()
        );
        int n = Math.max(dates.size(), Math.max(uses.size(), prices.size()));
        List<PaymentList> rows = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            LocalDate d = i < dates.size() ? dates.get(i) : null;
            String    u = i < uses.size()  ? uses.get(i)  : null;
            Long      p = i < prices.size()? prices.get(i): 0L;
            if (d == null && (u == null || u.isBlank()) && (p == null || p == 0L)) continue;
            rows.add(
                    PaymentList.builder()
                            .fkDraftSeq(draftSeq)
                            .regdate(d == null ? LocalDate.now() : d)
                            .content(u)
                            .price(p == null ? 0L : p)
                            .build()
            );
        }
        if (!rows.isEmpty()) paymentListRepository.saveAll(rows);
    }

    @Override
    public void saveBusiness(long draftSeq, String title, String content,
                             LocalDate start, LocalDate end, String location, String result) {
        businessRepository.save(
                Business.builder()
                        .draftSeq(draftSeq)
                        .businessTitle(title)
                        .businessContent(content)
                        .businessStart(start)
                        .businessEnd(end)
                        .businessLocation(location)
                        .businessResult(result)
                        .build()
        );
    }

    /* ===================== 요약/목록 ===================== */
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildInboxPreview(Long me, int limit) {
        var rows = draftLineRepository.findInbox(me);
        List<Map<String,Object>> preview = new ArrayList<>();
        for (var dl : rows) {
            var d = dl.getDraft();
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "-");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("isEmergency", d.getIsEmergency());
            m.put("regdate", d.getDraftRegdate()==null ? null :
                    java.util.Date.from(d.getDraftRegdate().atZone(ZONE).toInstant()));
            preview.add(m);
            if (preview.size() == limit) break;
        }
        return preview;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildSentPreview(Long me, int limit) {
        // 타입까지 미리 로딩되는 메서드로 교체
        var drafts = draftRepository.findByMemberWithType(me);

        List<Map<String,Object>> rows = new ArrayList<>();
        for (Draft d : drafts) {
            // 상태 계산
            var lines = draftLineRepository
                    .findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(d.getDraftSeq());
            boolean anyReject  = lines.stream().anyMatch(l -> Integer.valueOf(9).equals(l.getSignStatus()));
            boolean allApprove = !anyReject && lines.stream().allMatch(l -> Integer.valueOf(1).equals(l.getSignStatus()));
            int status = anyReject ? 9 : (allApprove ? 1 : 0);

            // JSP가 기대하는 키들을 정확히 넣기
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("docType",
                  (d.getDraftType()!=null && d.getDraftType().getDraftTypeName()!=null)
                        ? d.getDraftType().getDraftTypeName() : "-");   // 유형
            m.put("title", d.getDraftTitle());
            m.put("regdate",
                  d.getDraftRegdate()==null ? null :
                          java.util.Date.from(d.getDraftRegdate()
                          .atZone(java.time.ZoneId.of("Asia/Seoul")).toInstant()));
            m.put("isEmergency", d.getIsEmergency()==null ? 0 : d.getIsEmergency()); // ✅ 긴급
            m.put("status", status);

            rows.add(m);
            if (rows.size() == limit) break;
        }
        return rows;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildHistoryPreview(Long me, int limit) {
        var rows = draftLineRepository.findHistory(me); // 내 결재라인만, 가급적 signStatus in (1,9)
        List<Map<String,Object>> preview = new ArrayList<>(Math.min(limit, rows.size()));

        for (var dl : rows) {
            var d = dl.getDraft();
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("draftSeq", d.getDraftSeq());
            m.put("title", d.getDraftTitle());
            m.put("docType", d.getDraftType()!=null ? d.getDraftType().getDraftTypeName() : "문서");
            m.put("drafterName", d.getMember()!=null ? d.getMember().getMemberName() : "-");
            m.put("signDate", dl.getSignDate()==null ? null
                    : java.util.Date.from(dl.getSignDate().atZone(ZONE).toInstant()));
            m.put("myStatus", dl.getSignStatus()==null ? 0 : dl.getSignStatus()); // ★ 추가
            preview.add(m);
            if (preview.size() == limit) break;
        }
        return preview;
    }

    /* ===================== 상세 뷰 모델 ===================== */
    @Override
    @Transactional(readOnly = true)
    public SignView loadSignView(long draftSeq, long me) {
        // 1) 결재라인(안정적인 표시를 위해 정렬)
        List<DraftLine> lines = draftLineRepository.findLinesWithApprover(draftSeq);
        if (lines.isEmpty()) throw new IllegalArgumentException("결재라인이 존재하지 않습니다. draftSeq=" + draftSeq);
        lines.sort(java.util.Comparator.comparing(DraftLine::getLineOrder)); // 1,2,3,... (표시는 JSP에서 우->좌)

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

        // 3) 내 라인
        DraftLine myLine = draftLineRepository
                .findMyLine(Long.valueOf(draftSeq), Long.valueOf(me))
                .orElse(null);

        // ★ 변경: 다음 순번 = '대기(0)' 중 가장 큰 lineOrder (3→2→1 순서)
        Integer nextOrd = lines.stream()
                .filter(dl -> Integer.valueOf(0).equals(dl.getSignStatus()))
                .map(DraftLine::getLineOrder)
                .max(Integer::compareTo)
                .orElse(null);

        boolean canActNow = (myLine != null)
                && Integer.valueOf(0).equals(myLine.getSignStatus())
                && java.util.Objects.equals(myLine.getLineOrder(), nextOrd);

        boolean canEdit = (myLine != null);

        String docTypeName = (draft.getDraftType() != null && draft.getDraftType().getDraftTypeName() != null)
                ? draft.getDraftType().getDraftTypeName() : "문서";

        // 4) 내 도장
        String myStamp = "";
        try {
            String loginUserid = memberRepository.findById((int) me)
                    .map(Member::getMemberUserid)
                    .orElse(null);
            if (loginUserid != null && !loginUserid.isBlank()) {
                String saved = memberRepository.findStampImageByUserid(loginUserid);
                if (saved != null) myStamp = saved;
            }
        } catch (Exception ignore) {}

        // 5) 첨부 목록
        List<DraftFile> attachments =
                draftFileRepository.findByDraft_DraftSeqOrderByDraftFileSeqAsc(draftSeq);

        // 6) 모델 구성
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("draft", draft);
        model.put("lines", lines);
        model.put("canAct", canActNow);
        model.put("canEdit", canEdit);
        model.put("myDraftLineSeq", (myLine != null ? myLine.getDraftLineSeq() : null));
        model.put("loginMemberSeq", me);
        model.put("docTypeName", docTypeName);
        model.put("myStampImage", myStamp);
        model.put("attachments", attachments);

        // 서브 엔티티
        vacationRepository.findByDraftSeq(draftSeq).ifPresent(v -> model.put("vacation", v));
        paymentRepository.findByDraftSeq(draftSeq).ifPresent(p -> {
            model.put("payment", p);
            List<PaymentList> items =
                    paymentListRepository.findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(draftSeq);
            model.put("paymentLists", items);
        });

        // ★ 추가: 출장(보고서) — JSP에서 ${business.*}로 사용
        businessRepository.findByDraftSeq(draftSeq).ifPresent(b -> model.put("business", b));

        return new SignView(model);
    }



    /* ===================== 승인/반려 ===================== */
    @Override
    public ApproveResult approve(long draftLineSeq, Long approverSeq, String comment) {
        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, approverSeq).orElse(null);
        if (mine == null) return new ApproveResult(false, null, "내 결재선이 아닙니다.");

        Draft d = anyLine.getDraft();
        int prevStatus = (d.getDraftStatus()==null ? 0 : d.getDraftStatus());

        boolean editing = (mine.getSignStatus()!=null && mine.getSignStatus()!=0);
        if (!editing) {
            Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
            if (nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
                return new ApproveResult(false, null, "지금은 결재할 수 없는 상태입니다.");
            }
        }

        Integer oldStatus  = mine.getSignStatus();
        String  oldComment = mine.getSignComment();

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

        // 알림
        try {
            String link = "/sign/view/" + draftSeq;
            String docTitle = d.getDraftTitle();
            String approverName = getMemberName(approverSeq);
            boolean edited = (oldStatus != null && oldStatus != 0);
            boolean commentChanged = !Objects.equals(oldComment, comment);

            if (allApprove) {
                pushToUser(d.getMember(),
                        (edited && oldStatus == 9) ? "전자결재 최종 승인(반려→승인)" : "전자결재 최종 승인",
                        "「" + docTitle + "」 문서가 최종 승인되었습니다.",
                        link,
                        "appr_final_" + draftSeq
                );
            } else {
                String title = (edited && oldStatus == 9) ? "전자결재 승인(반려→승인)" : "전자결재 승인";
                pushToUser(d.getMember(),
                        title,
                        approverName + " 님이 「" + docTitle + "」 문서를 승인했습니다.",
                        link,
                        "appr_step_" + draftSeq + "_" + mine.getLineOrder()
                );
                if (edited && oldStatus == 1 && commentChanged) {
                    pushToUser(d.getMember(),
                            "결재 의견 수정",
                            approverName + " 님이 「" + docTitle + "」 의견을 수정했습니다.",
                            link,
                            "appr_comment_update_" + draftSeq + "_" + mine.getLineOrder()
                    );
                }
            }

            Integer nextOrdAfter = draftLineRepository.findNextOrder(draftSeq);
            if (!allApprove && nextOrdAfter != null) {
                DraftLine nextLine = lines.stream()
                        .filter(l -> nextOrdAfter.equals(l.getLineOrder()))
                        .findFirst().orElse(null);
                if (nextLine != null && nextLine.getApprover() != null) {
                    pushToUser(nextLine.getApprover(),
                            (edited && oldStatus == 9) ? "결재 요청 도착(재개)" : "결재 요청 도착",
                            "「" + docTitle + "」 결재 대기 (" + nextOrdAfter + "단계)",
                            link,
                            "req_" + draftSeq + "_" + nextOrdAfter
                    );
                }
            }
        } catch (Exception ignore) {}

        return new ApproveResult(true, mine.getDraftLineSeq(), null);
    }

    @Override
    public RejectResult reject(long draftLineSeq, Long approverSeq, String comment) {
        DraftLine anyLine = draftLineRepository.findById(draftLineSeq).orElseThrow();
        Long draftSeq = anyLine.getDraft().getDraftSeq();

        DraftLine mine = draftLineRepository.findMyLine(draftSeq, approverSeq).orElse(null);
        if (mine == null) return new RejectResult(false, null, "내 결재선이 아닙니다.");

        Draft d = anyLine.getDraft();
        int prevStatus = (d.getDraftStatus()==null ? 0 : d.getDraftStatus());

        boolean editing = (mine.getSignStatus()!=null && mine.getSignStatus()!=0);
        if (!editing) {
            Integer nextOrd = draftLineRepository.findNextOrder(draftSeq);
            if (nextOrd == null || !mine.getLineOrder().equals(nextOrd)) {
                return new RejectResult(false, null, "지금은 결재할 수 없는 상태입니다.");
            }
        }

        Integer oldStatus  = mine.getSignStatus();
        String  oldComment = mine.getSignComment();

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

        try {
            String link = "/sign/view/" + draftSeq;
            String docTitle = d.getDraftTitle();
            String approverName = getMemberName(approverSeq);
            boolean edited = (oldStatus != null && oldStatus != 0);
            boolean commentChanged = !Objects.equals(oldComment, comment);

            String reason = (comment == null || comment.isBlank()) ? ""
                    : (" (사유: " + (comment.length() > 60 ? comment.substring(0,60) + "…" : comment) + ")");

            String title;
            String body;
            String notiId;
            if (edited && oldStatus == 1) {
                title = "전자결재 반려(승인→반려)";
                body  = approverName + " 님이 「" + docTitle + "」 문서를 승인에서 반려로 변경했습니다." + reason;
                notiId = "reject_change_" + draftSeq;
            } else if (edited && oldStatus == 9 && commentChanged) {
                title = "전자결재 반려 의견 수정";
                body  = approverName + " 님이 「" + docTitle + "」 반려 의견을 수정했습니다." + reason;
                notiId = "reject_update_" + draftSeq;
            } else {
                title = "전자결재 반려";
                body  = approverName + " 님이 「" + docTitle + "」 문서를 반려했습니다." + reason;
                notiId = "reject_" + draftSeq;
            }

            pushToUser(d.getMember(), title, body, link, notiId);

            if (edited && oldStatus == 1) {
                DraftLine nextCandidate = lines.stream()
                        .filter(l -> Integer.valueOf(0).equals(l.getSignStatus()))
                        .sorted(Comparator.comparing(DraftLine::getLineOrder))
                        .findFirst().orElse(null);
                if (nextCandidate != null && nextCandidate.getApprover() != null) {
                    pushToUser(nextCandidate.getApprover(),
                            "결재 요청 취소",
                            "「" + docTitle + "」 결재 요청이 상위 단계에서 반려되어 취소되었습니다.",
                            link,
                            "req_cancel_" + draftSeq + "_" + nextCandidate.getLineOrder()
                    );
                }
            }
        } catch (Exception ignore) {}

        return new RejectResult(true, mine.getDraftLineSeq(), null);
    }

    private void applyVacationDeductionIfNeeded(Draft draft) {
        var optVac = vacationRepository.findByDraftSeq(draft.getDraftSeq());
        if (optVac.isEmpty()) return;
        var v = optVac.get();
        BigDecimal useDays = calcVacationDays(v);

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
        BigDecimal useDays = calcVacationDays(v);

        em.createNativeQuery(
                "UPDATE TBL_ANNUAL_LEAVE " +
                        "   SET USED_LEAVE = USED_LEAVE - :d, " +
                        "       REMAINING_LEAVE = REMAINING_LEAVE + :d " +
                        " WHERE MEMBER_SEQ = :m"
        ).setParameter("d", useDays)
         .setParameter("m", draft.getMember().getMemberSeq())
         .executeUpdate();
    }

    private BigDecimal calcVacationDays(Vacation v) {
        if ("HALF".equalsIgnoreCase(v.getVacationType())) return new BigDecimal("0.5");
        long days = java.time.temporal.ChronoUnit.DAYS.between(v.getVacationStart(), v.getVacationEnd()) + 1;
        return new BigDecimal(days);
    }

    /* ===================== 도장 파일 ===================== */
    @Override
    public String saveStamp(String userid, String originalName, InputStream is, String saveDir) {
        try {
            File dir = new File(saveDir);
            if (!dir.exists()) dir.mkdirs();
            String newFilename = fileManager.doFileUpload(is, originalName, saveDir);
            memberRepository.stampImageSave(userid, newFilename);
            return newFilename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteStamp(String userid, String saveDir) {
        String savedFilename = memberRepository.findStampImageByUserid(userid);
        if (savedFilename != null && !savedFilename.isBlank()) {
            try { fileManager.doFileDelete(savedFilename, saveDir); } catch (Exception ignore) {}
        }
        memberRepository.clearStampImageByUserid(userid);
    }

    /* ===================== 첨부 다운로드 스트리밍 ===================== */
    @Override
    @Transactional(readOnly = true)
    public void streamAttachment(Long draftFileId, String webRootRealPath,
                                 HttpServletResponse response) throws java.io.IOException {
        DraftFile f = draftFileRepository.findById(draftFileId).orElse(null);
        if (f == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String webPath = f.getFilePath(); // e.g. /resources/edoc_upload/uuid.ext
        String real = webPath.startsWith("/") ? webRootRealPath + webPath.substring(1) : webRootRealPath + webPath;

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

    /* ===================== 엑셀 다운로드(간단 버전) ===================== */
    @Override
    @Transactional(readOnly = true)
    public void exportDraftToExcel(Long draftSeq, Model model) {
        // 필요한 데이터를 모델에 담아 View(예: excelDownloadView)에서 처리
        Draft draft = draftRepository.findById(draftSeq).orElseThrow();
        List<DraftLine> lines = draftLineRepository.findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(draftSeq);
        model.addAttribute("draft", draft);
        model.addAttribute("lines", lines);
        vacationRepository.findByDraftSeq(draftSeq).ifPresent(v -> model.addAttribute("vacation", v));
        paymentRepository.findByDraftSeq(draftSeq).ifPresent(p -> {
            model.addAttribute("payment", p);
            List<PaymentList> items =
                    paymentListRepository.findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(draftSeq);
            model.addAttribute("paymentLists", items);
        });
        businessRepository.findByDraftSeq(draftSeq).ifPresent(b -> model.addAttribute("business", b));
        businessConformRepository.findByDraftSeq(draftSeq).ifPresent(c -> model.addAttribute("conform", c));
    }

    /* ===================== 푸시 알림 ===================== */
    private String getMemberName(Long memberSeq) {
        if (memberSeq == null) return "-";
        return memberRepository.findById(memberSeq.intValue())
                .map(Member::getMemberName)
                .orElse("-");
    }

    private void pushToUser(Member target, String title, String body, String link, String notiId) {
        if (target == null) return;

        String userid = null;
        try {
            userid = (String) Member.class.getMethod("getMemberUserid").invoke(target);
        } catch (Exception ignore) {
            try { userid = (String) Member.class.getMethod("getMemberId").invoke(target); }
            catch (Exception ignore2) {}
        }
        if (userid == null || userid.isBlank()) return;

        wsHandler.pushNotify(userid,
                new WebsocketEchoHandler.NotifyPayload(
                        "notify",
                        title,
                        body,
                        link,
                        notiId,
                        java.time.OffsetDateTime.now().toString()
                )
        );
    }
}
