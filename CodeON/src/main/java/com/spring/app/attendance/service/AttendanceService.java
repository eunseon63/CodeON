package com.spring.app.attendance.service;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.attendance.domain.WorkSummary;

import java.time.YearMonth;
import java.util.List;

public interface AttendanceService {
    void startWork(int memberSeq);
    void endWork(int memberSeq);
    List<AttendanceRecord> getMonthly(int memberSeq, YearMonth ym);
    WorkSummary getMonthlySummary(int memberSeq, YearMonth ym);
}
