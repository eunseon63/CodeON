// com.spring.app.service.SignService_imple.java
package com.spring.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import org.hibernate.LazyInitializationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.spring.app.entity.*;
import com.spring.app.model.*;

@Service
@RequiredArgsConstructor
public class SignService_imple implements SignService {

    private final DraftRepository draftRepository;
    private final DraftLineRepository draftLineRepository;
    private final DraftFileRepository draftFileRepository;

    private final BusinessRepository businessRepository;
    private final BusinessConformRepository businessConformRepository;
    private final VacationRepository vacationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentListRepository paymentListRepository;

    // LAZY 안전용 (부서/직급명 보정)
    private final DepartmentRepository departmentRepository;
    private final GradeRepository gradeRepository;

    private static final DateTimeFormatter DTF_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public void exportDraftToExcel(Long draftSeq, Model model) {
        Draft draft = draftRepository.findByIdWithMemberAndType(draftSeq).orElseThrow();

        String docTypeName = nvl(draft.getDraftType() != null ? draft.getDraftType().getDraftTypeName() : "");
        String drafter     = draft.getMember() != null ? nvl(draft.getMember().getMemberName()) : "";
        String regDateStr  = toDateString(draft.getDraftRegdate());

        SXSSFWorkbook wb = new SXSSFWorkbook();
        SXSSFSheet sheet = wb.createSheet("결재 문서");

        // ---------- 스타일 ----------
        Font fontTitle = wb.createFont(); fontTitle.setFontHeightInPoints((short)16); fontTitle.setBold(true);
        Font fontLabel = wb.createFont(); fontLabel.setBold(true);
        Font fontBody  = wb.createFont();

        CellStyle sTitle = wb.createCellStyle();
        sTitle.setAlignment(HorizontalAlignment.CENTER);
        sTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        sTitle.setFont(fontTitle);
        sTitle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        sTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBoxBorder(sTitle);

        CellStyle sHead = wb.createCellStyle();
        sHead.setFont(fontLabel);
        sHead.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        sHead.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addThinBoxBorder(sHead);

        CellStyle sCell = wb.createCellStyle();
        sCell.setFont(fontBody);
        sCell.setVerticalAlignment(VerticalAlignment.CENTER);
        addThinBoxBorder(sCell);

        // 긴 텍스트용 (줄바꿈 + 상단정렬)
        CellStyle sWrapTop = wb.createCellStyle();
        sWrapTop.cloneStyleFrom(sCell);
        sWrapTop.setWrapText(true);
        sWrapTop.setVerticalAlignment(VerticalAlignment.TOP);

        CellStyle sRight = wb.createCellStyle();
        sRight.cloneStyleFrom(sCell);
        sRight.setAlignment(HorizontalAlignment.RIGHT);

        // 공통 열 너비
        sheet.setColumnWidth(0, 4200);
        sheet.setColumnWidth(1, 9500);
        sheet.setColumnWidth(2, 4200);
        sheet.setColumnWidth(3, 4600);

        int r = 0;

        // ---------- 문서 머리 ----------
        r = makeMergedTitle(sheet, r, sTitle, docTypeName + " 상세");

        // ---------- 메타 ----------
        r = writeMetaTable(sheet, r, sHead, sCell, sWrapTop, draft);

        // ---------- 본문 ----------
        r = makeSectionHeader(sheet, r, sHead, "본문");
        String typeKey = (docTypeName == null ? "" : docTypeName).replaceAll("\\s+", "");
        switch (typeKey) {
            case "휴가신청":
            case "휴가신청서": {
                Vacation v = vacationRepository.findByDraftSeq(draftSeq).orElse(null);
                r = writeVacationBody(sheet, r, sCell, sWrapTop, v);
                break;
            }
            case "지출결의":
            case "지출결의서": {
                Payment p = paymentRepository.findByDraftSeq(draftSeq).orElse(null);
                List<PaymentList> lists =
                        paymentListRepository.findByFkDraftSeqOrderByRegdateAscPaymentListSeqAsc(draftSeq);
                r = writePaymentBody(sheet, r, sHead, sCell, sRight, sWrapTop, p, lists);
                break;
            }
            case "업무기안":
            case "업무기안서": {
                Business b = businessRepository.findByDraftSeq(draftSeq).orElse(null);
                r = writeBusinessBody(sheet, r, sCell, sWrapTop, b);
                break;
            }
            case "출장/업무확인":
            case "출장업무확인":
            case "출장업무확인서": {
                BusinessConform c = businessConformRepository.findByDraftSeq(draftSeq).orElse(null);
                r = writeConformBody(sheet, r, sCell, sWrapTop, c);
                break;
            }
            default: {
                Row row = sheet.createRow(r++);
                cell(row, 0, sCell, "내용");
                cell(row, 1, sWrapTop, nvl(draft.getDraftContent()));
                mergeWithBoxBorder(sheet, row, 1, 3);
                autoHeightForMerged(sheet, row, 1, 3, nvl(draft.getDraftContent()));
                break;
            }
        }

        // ---------- 결재선 ----------
        r = makeSectionHeader(sheet, r, sHead, "결재선");
        List<DraftLine> lines = draftLineRepository.findByDraft_DraftSeqOrderByLineOrderAsc(draftSeq);
        r = writeLinesTable(sheet, r, sHead, sCell, lines);

        // ---------- 첨부 ----------
        List<DraftFile> files = draftFileRepository.findByDraft_DraftSeqOrderByDraftFileSeqAsc(draftSeq);
        if (!files.isEmpty()) {
            r = makeSectionHeader(sheet, r, sHead, "첨부파일");
            r = writeFilesTable(sheet, r, sHead, sCell, files);
        }

        // ---------- 파일명 ----------
        String fileBase = String.format("%s_%s_%s", safe(docTypeName), safe(drafter), safe(regDateStr));
        model.addAttribute("locale", Locale.KOREA);
        model.addAttribute("workbookName", fileBase);
        model.addAttribute("workbook", wb);
    }

    // ====== 섹션 유틸 ======
    private int makeMergedTitle(SXSSFSheet sh, int r, CellStyle style, String text){
        Row row = sh.createRow(r++);
        row.setHeightInPoints(28);
        cell(row,0,style,text);
        mergeWithBoxBorder(sh, row, 0, 3);
        return r;
    }

    private int makeSectionHeader(SXSSFSheet sh, int r, CellStyle head, String title){
        Row row = sh.createRow(r++);
        cell(row,0,head,title);
        mergeWithBoxBorder(sh, row, 0, 3);
        return r;
    }

    private int writeMetaTable(SXSSFSheet sh, int r, CellStyle head, CellStyle cell, CellStyle wrapTop, Draft d){
        // 기안자/소속
        Row r1 = sh.createRow(r++);
        cell(r1,0,head,"기안자"); cell(r1,1,cell, nvl(d.getMember()!=null? d.getMember().getMemberName(): ""));
        cell(r1,2,head,"소속");   cell(r1,3,cell, deptName(d));

        // 기안일/문서번호
        Row r2 = sh.createRow(r++);
        cell(r2,0,head,"기안일"); cell(r2,1,cell, toDateString(d.getDraftRegdate()));
        cell(r2,2,head,"문서번호");cell(r2,3,cell, String.valueOf(d.getDraftSeq()));

        // 제목(긴 텍스트 자동 높이)
        Row r3 = sh.createRow(r++);
        cell(r3,0,head,"제목");
        cell(r3,1,wrapTop, nvl(d.getDraftTitle()));
        mergeWithBoxBorder(sh, r3, 1, 3);
        autoHeightForMerged(sh, r3, 1, 3, nvl(d.getDraftTitle()));
        return r;
    }

    private int writeVacationBody(SXSSFSheet sh, int r, CellStyle sCell, CellStyle sWrapTop, Vacation v){
        // 기간
        Row a = sh.createRow(r++);
        cell(a, 0, sCell, "기간");
        String start = toDate(v != null ? v.getVacationStart() : null);
        String end   = toDate(v != null ? v.getVacationEnd()   : null);
        String kind  = (v != null && "HALF".equalsIgnoreCase(nvl(v.getVacationType()))) ? "반차" : "연차";
        String period = (start.isEmpty() && end.isEmpty()) ? "" : start + " ~ " + end + " (" + kind + ")";
        cell(a, 1, sCell, period);
        mergeWithBoxBorder(sh, a, 1, 3);

        // 내용(줄바꿈 + 자동 높이)
        Row b = sh.createRow(r++);
        cell(b, 0, sCell, "내용");
        String body = v == null ? "" : nvl(v.getVacationContent());
        cell(b, 1, sWrapTop, body);
        mergeWithBoxBorder(sh, b, 1, 3);
        autoHeightForMerged(sh, b, 1, 3, body);
        return r;
    }

    private int writePaymentBody(
            SXSSFSheet sh, int r, CellStyle head, CellStyle cell, CellStyle right, CellStyle wrapTop,
            Payment p, List<PaymentList> lists) {

        if (p == null) return r;

        // 지출 상세용 컬럼 폭
        sh.setColumnWidth(0, 4200);   // 일자
        sh.setColumnWidth(1, 10000);  // 사용처
        sh.setColumnWidth(2, 7000);   // 금액(넓게)

        // 금액 서식
        CellStyle money = sh.getWorkbook().createCellStyle();
        money.cloneStyleFrom(right);
        DataFormat df = sh.getWorkbook().createDataFormat();
        money.setDataFormat(df.getFormat("#,##0"));

        // 제목/사유 (병합 + 자동 높이)
        Row t = sh.createRow(r++);
        cell(t, 0, cell, "지출 제목");
        cell(t, 1, wrapTop, nvl(p.getPaymentTitle()));
        mergeWithBoxBorder(sh, t, 1, 3);
        autoHeightForMerged(sh, t, 1, 3, nvl(p.getPaymentTitle()));

        Row r2 = sh.createRow(r++);
        cell(r2, 0, cell, "지출 사유");
        cell(r2, 1, wrapTop, nvl(p.getPaymentContent()));
        mergeWithBoxBorder(sh, r2, 1, 3);
        autoHeightForMerged(sh, r2, 1, 3, nvl(p.getPaymentContent()));

        // 헤더
        Row h = sh.createRow(r++);
        cell(h, 0, head, "지출일자");
        cell(h, 1, head, "사용처");
        cell(h, 2, head, "금액");

        long total = 0L;
        if (lists != null) {
            for (PaymentList pl : lists) {
                Row row = sh.createRow(r++);
                cell(row, 0, cell, toDate(pl.getRegdate()));
                cell(row, 1, cell, nvl(pl.getContent()));
                cell(row, 2, money, pl.getPrice() == null ? 0 : pl.getPrice()); // 숫자 셀
                total += pl.getPrice() == null ? 0 : pl.getPrice();
            }
        }

        Row ft = sh.createRow(r++);
        cell(ft, 0, head, "합계");
        mergeWithBoxBorder(sh, ft, 0, 1); // A~B 병합
        cell(ft, 2, money, total);
        return r;
    }

    private int writeBusinessBody(SXSSFSheet sh, int r, CellStyle cell, CellStyle wrapTop, Business b){
        if (b == null) return r;

        Row a = sh.createRow(r++);
        cell(a,0,cell,"기간");
        cell(a,1,cell, toDate(b.getBusinessStart()) + " ~ " + toDate(b.getBusinessEnd()));
        mergeWithBoxBorder(sh, a, 1, 3);

        Row b1 = sh.createRow(r++);
        cell(b1,0,cell,"장소");
        cell(b1,1,cell, nvl(b.getBusinessLocation()));
        mergeWithBoxBorder(sh, b1, 1, 3);

        Row c = sh.createRow(r++);
        cell(c,0,cell,"내용");
        String body = nvl(b.getBusinessContent());
        cell(c,1,wrapTop, body);
        mergeWithBoxBorder(sh, c, 1, 3);
        autoHeightForMerged(sh, c, 1, 3, body);

        Row d = sh.createRow(r++);
        cell(d,0,cell,"결과");
        String res = nvl(b.getBusinessResult());
        cell(d,1,wrapTop, res);
        mergeWithBoxBorder(sh, d, 1, 3);
        autoHeightForMerged(sh, d, 1, 3, res);

        return r;
    }

    private int writeConformBody(SXSSFSheet sh, int r, CellStyle cell, CellStyle wrapTop, BusinessConform c){
        if (c == null) return r;

        Row a = sh.createRow(r++);
        cell(a,0,cell,"제목");
        cell(a,1,wrapTop, nvl(c.getConformTitle()));
        mergeWithBoxBorder(sh, a, 1, 3);
        autoHeightForMerged(sh, a, 1, 3, nvl(c.getConformTitle()));

        Row b = sh.createRow(r++);
        cell(b,0,cell,"내용");
        cell(b,1,wrapTop, nvl(c.getConformContent()));
        mergeWithBoxBorder(sh, b, 1, 3);
        autoHeightForMerged(sh, b, 1, 3, nvl(c.getConformContent()));

        return r;
    }

    private int writeLinesTable(SXSSFSheet sh, int r, CellStyle head, CellStyle cell, List<DraftLine> lines){
        Row h = sh.createRow(r++);
        cell(h,0,head,"결재자");
        cell(h,1,head,"직급");
        cell(h,2,head,"상태");
        cell(h,3,head,"처리일");

        if (lines != null) {
            for (DraftLine l : lines){
                Row row = sh.createRow(r++);
                Member ap = l.getApprover();
                String name  = ap != null ? nvl(ap.getMemberName()) : "";
                String grade = gradeName(ap);
                cell(row,0,cell, name);
                cell(row,1,cell, grade);
                cell(row,2,cell, statusKo(l.getSignStatus()));
                cell(row,3,cell, toDateTime(l.getSignDate()));
            }
        }
        return r;
    }

    private int writeFilesTable(SXSSFSheet sh, int r, CellStyle head, CellStyle cell, List<DraftFile> files){
        Row h = sh.createRow(r++);
        cell(h,0,head,"파일명");
        mergeWithBoxBorder(sh, h, 0, 3);
        for (DraftFile f : files){
            Row row = sh.createRow(r++);
            cell(row,0,cell, nvl(f.getFileName()));
            mergeWithBoxBorder(sh, row, 0, 3);
        }
        return r;
    }

    // ====== 보조 유틸 ======
    private static void addBoxBorder(CellStyle st){
        st.setBorderTop(BorderStyle.MEDIUM);
        st.setBorderRight(BorderStyle.MEDIUM);
        st.setBorderBottom(BorderStyle.MEDIUM);
        st.setBorderLeft(BorderStyle.MEDIUM);
    }
    private static void addThinBoxBorder(CellStyle st){
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
    }
    private static Cell cell(Row r, int c, CellStyle s, String v){
        Cell cell = r.createCell(c);
        cell.setCellStyle(s);
        cell.setCellValue(v != null ? v : "");
        return cell;
    }
    private static Cell cell(Row r, int c, CellStyle s, long v){
        Cell cell = r.createCell(c);
        cell.setCellStyle(s);
        cell.setCellValue((double) v); // 숫자는 double
        return cell;
    }
    private static String nvl(String s){ return s == null ? "" : s; }
    private static String safe(String s){ return nvl(s).replaceAll("[\\\\/:*?\"<>|\\s]+","_"); }

    private String deptName(Draft d){
        Member m = d.getMember();
        if (m == null) return "";
        try {
            if (m.getDepartment() != null) return nvl(m.getDepartment().getDepartmentName());
        } catch (LazyInitializationException ignore) {}
        return departmentRepository.findById((long)m.getFkDepartmentSeq())
                .map(Department::getDepartmentName).orElse("");
    }
    private String gradeName(Member m){
        if (m == null) return "";
        try {
            if (m.getGrade() != null) return nvl(m.getGrade().getGradeName());
        } catch (LazyInitializationException ignore) {}
        return gradeRepository.findById(Integer.valueOf(m.getFkGradeSeq()))
                .map(Grade::getGradeName)
                .orElse("");
    }

    private static String statusKo(Integer s){
        if (s == null) return "대기";
        return switch (s) {
            case 1 -> "승인";
            case 9 -> "반려";
            default -> "대기";
        };
    }
    private static String toDate(LocalDate d){ return d == null ? "" : d.format(DTF_DATE); }
    private static String toDate(LocalDateTime dt){ return dt == null ? "" : dt.format(DTF_DATE); }
    private static String toDateTime(LocalDateTime dt){ return dt == null ? "" : dt.format(DTF_DATETIME); }
    private static String toDateString(Object maybe){
        if (maybe == null) return "";
        if (maybe instanceof LocalDateTime ldt) return ldt.format(DTF_DATE);
        if (maybe instanceof LocalDate ld) return ld.format(DTF_DATE);
        return String.valueOf(maybe);
    }

    /**
     * 병합된 셀( firstCol ~ lastCol ) 텍스트 길이에 맞춰 행 높이 계산.
     * 엑셀은 병합된 셀 자동높이를 지원하지 않아서 수동 계산.
     * 컬럼너비를 문자 폭으로 환산하고, 한글 등 비ASCII는 2폭으로 가정.
     */
    private static void autoHeightForMerged(SXSSFSheet sh, Row row, int firstCol, int lastCol, String text){
        if (text == null) text = "";
        int capacity = 0;
        for (int c = firstCol; c <= lastCol; c++) {
            capacity += sh.getColumnWidth(c) / 256; // 문자 폭 환산
        }
        if (capacity <= 0) capacity = 1;

        String[] logicalLines = text.split("\\r?\\n", -1);
        int neededLines = 0;
        for (String line : logicalLines) {
            int units = 0;
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                units += (ch <= 0x007F) ? 1 : 2; // 비ASCII 2폭
            }
            int n = Math.max(1, (int)Math.ceil((double)units / capacity));
            neededLines += n;
        }
        float base = sh.getDefaultRowHeightInPoints(); // 보통 15pt
        row.setHeightInPoints(Math.max(base, neededLines * base));
    }

    /** 병합 + 경계선 끊김 방지용 보더 일괄적용 */
    private static void mergeWithBoxBorder(Sheet sh, Row row, int firstCol, int lastCol) {
        CellRangeAddress region =
            new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol);
        sh.addMergedRegion(region);
        RegionUtil.setBorderTop(BorderStyle.THIN, region, sh);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sh);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sh);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sh);
    }
}
