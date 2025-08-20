package com.spring.app.attendance.service;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.attendance.domain.WorkSummary;
import com.spring.app.attendance.model.AttendanceDAO;
import com.spring.app.domain.AnnualLeaveDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService_imple implements AttendanceService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final int STANDARD_MIN = 480; // 8시간 기준

    private final AttendanceDAO attendanceDAO;

    @Transactional
    @Override
    public void startWork(int memberSeq) {
        LocalDate today = LocalDate.now(ZONE);	// 한국 기준으로 오늘 날짜(LocalDate)를 가져옴. 시간은 제와하고 년-월-일만.
        // 오늘 날짜와 해당 직원 번호로 근태 테이블 조회.
        // 반환값이 없으면 -> 오늘 첫 기록이 없음
        // 반환값이 있으면 -> 이미 출근 또는 퇴근 기록이 있을 수 있음
        var rec = attendanceDAO.selectByMemberAndDate(memberSeq, today);
        if (rec == null) {
        	// 오늘 처음 출근 기록
            attendanceDAO.insertStart(memberSeq, today);
        } else if (rec.getStartTime() == null) {
        	// 오늘 기록은 있으나 출근 시간이 아직 없음 -> 업데이트
            attendanceDAO.updateStartIfNull(memberSeq, today);
        }
    }

    @Transactional
    @Override
    public void endWork(int memberSeq) {
        LocalDate today = LocalDate.now(ZONE);
        // 오늘 날짜와 해당 직원 번호로 근태 테이블 조회.
        var rec = attendanceDAO.selectByMemberAndDate(memberSeq, today);
        if (rec == null || rec.getStartTime() == null) {
            throw new IllegalStateException("출근 기록이 없습니다.");	// 롤백 예외 발생
        }
        // 출근 기록이 있으면 퇴근 기록
        LocalDateTime now = LocalDateTime.now(ZONE);	// 퇴근시간으로 현재 시간을 저장
        // 출근 시간(rec.getStartTime())과 현재 시간(now) 사이의 분 단위 근무 시간 계산
        long worked = ChronoUnit.MINUTES.between(rec.getStartTime(), now);
        // 초과근무 계산. STANDARD_MIN = 480분(8시간) 기준.
        // 실제 근무 시간 worked가 기준보다 많으면 그 차이를 초과근무 시간으로 설정.
        // 음수가 되지 않도록 Math.max(0, …) 사용.
        int overtime = (int) Math.max(0, worked - STANDARD_MIN);
        attendanceDAO.updateEnd(memberSeq, today, now, overtime);
    }

    @Override
    public List<AttendanceRecord> getMonthly(int memberSeq, YearMonth ym) {
        List<AttendanceRecord> list = attendanceDAO.selectMonthly(memberSeq, ym);
        for (AttendanceRecord att : list) {
            if (att.getStartTime() != null && att.getEndTime() != null) {
                long minutes = ChronoUnit.MINUTES.between(att.getStartTime(), att.getEndTime());
                att.setWorkedMinutes((int) minutes);

                long h = minutes / 60;
                long m = minutes % 60;
                att.setWorkedTimeStr(String.format("%02d:%02d", h, m));
            } else {
                att.setWorkedMinutes(null);
                att.setWorkedTimeStr(null);
            }
        }
        return list;
    }
    
    private static String toHHmm(int minutes) {
        int h = minutes / 60, m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }

    public WorkSummary getMonthlySummary(int memberSeq, YearMonth ym) {
        Map<String, Object> m = attendanceDAO.selectMonthlySummary(memberSeq, ym);

        // ✅ 결과 행 자체가 없을 때 방어
        if (m == null) {
            WorkSummary s = new WorkSummary();
            s.setTotalMinutes(0);
            s.setTotalMinutesStr("00:00");
            s.setWorkDays(0);
            s.setTotalOvertime(0);
            return s;
        }

        // ✅ 각 컬럼이 NULL일 수도 있으니 안전 변환
        int totalMinutes  = toInt(m.get("TOTAL_MINUTES"));
        int workDays      = toInt(m.get("WORK_DAYS"));
        int totalOvertime = toInt(m.get("TOTAL_OVERTIME"));

        WorkSummary s = new WorkSummary();
        s.setTotalMinutes(totalMinutes);
        s.setTotalMinutesStr(toHHmm(totalMinutes));
        s.setWorkDays(workDays);
        s.setTotalOvertime(totalOvertime);
        return s;
    }

    private static int toInt(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception e) { return 0; }
    }

    @Override
    public AnnualLeaveDTO getAnnualLeave(int memberSeq) {
        AnnualLeaveDTO dto = attendanceDAO.selectAnnualLeaveByMemberSeq(memberSeq);
        if (dto == null) {
            dto = AnnualLeaveDTO.builder()
                    .memberSeq(memberSeq)
                    .totalLeave(0).usedLeave(0).remainingLeave(0)
                    .build();
        }
        return dto;
    }


}
