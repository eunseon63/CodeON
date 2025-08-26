package com.spring.app.controller;

import com.spring.app.attendance.domain.AttendanceRecord;
import com.spring.app.attendance.service.AttendanceService;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.MemberProfileDTO;
import com.spring.app.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class FrontController {

    private final AttendanceService attendanceService;
    private final MyPageService myPageService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @GetMapping("")
    public String start() {
        return "redirect:/login/loginStart";
    }

    @GetMapping("index")
    public String index(Model model,
                        @SessionAttribute(name = "loginuser", required = false) MemberDTO loginuser,
                        HttpServletRequest request,
                        RedirectAttributes ra) {

        if (loginuser == null) {
            ra.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:" + request.getContextPath() + "/login/loginStart";
        }

        int memberSeq = loginuser.getMemberSeq();
        String userName = loginuser.getMemberName();

        MemberProfileDTO profile = myPageService.getProfile(memberSeq);
        String gradeName = (profile != null && profile.getGradeName() != null) ? profile.getGradeName() : "-";

        LocalDate today = LocalDate.now(KST);
        List<AttendanceRecord> monthList = attendanceService.getMonthly(memberSeq, YearMonth.now());

        // 오늘 레코드(문자열 비교를 위해 workDateStr 사용)
        AttendanceRecord todayRec = monthList.stream()
                .filter(r -> today.toString().equals(r.getWorkDateStr()))
                .findFirst()
                .orElse(null);

        String startTimeStr = (todayRec != null && todayRec.getStartTimeStr() != null) ? todayRec.getStartTimeStr() : "-";
        String endTimeStr   = (todayRec != null && todayRec.getEndTimeStr()   != null) ? todayRec.getEndTimeStr()   : "-";

        model.addAttribute("userName", userName);
        model.addAttribute("gradeName", gradeName);
        model.addAttribute("todayStr", today.toString());
        model.addAttribute("startTimeStr", startTimeStr);
        model.addAttribute("endTimeStr", endTimeStr);

        // 출퇴근 박스에서 forEach를 그대로 쓰고 싶다면 전달
        model.addAttribute("attendanceList", monthList);

        return "index"; // /WEB-INF/views/index.jsp
    }
}
