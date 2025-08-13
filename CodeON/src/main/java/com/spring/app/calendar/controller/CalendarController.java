package com.spring.app.calendar.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.calendar.service.CalendarService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Calendar")  // 소문자 통일
public class CalendarController {

    private final CalendarService service;

    // 생성자 주입
    public CalendarController(CalendarService service) {
        this.service = service;
    }
    
    @GetMapping("/list")
    public String calendarList() {
        return "Calendar/list";  // JSP 경로도 소문자 폴더명으로 맞춤
    }
    
    @GetMapping("/addCalendarForm")
    public String calendarAddCalendarForm() {
        return "Calendar/addCalendarForm";  // JSP 경로
    }

    // === 일정 등록 처리 ===
    @PostMapping("/addCalendarForm")
    public ModelAndView addCalendarEvent(ModelAndView mav, HttpServletRequest request) {

    	HttpSession session = request.getSession();
    	String memberSeq = (String) session.getAttribute("memberSeq");  // 세션에서 꺼내기

	    if(memberSeq == null) {
	        mav.addObject("message", "로그인이 필요합니다.");
	        mav.addObject("loc", request.getContextPath() + "/views/login/login.jsp");  // 로그인 페이지로 리다이렉트
	        mav.setViewName("msg");
	        return mav;
    	}
    	
        String startDate = request.getParameter("startDate"); // yyyy-MM-ddTHH:mm
        String endDate = request.getParameter("endDate");
        String title = request.getParameter("title");
        String calendarType = request.getParameter("calendarType");
        String shareTargets = request.getParameter("shareTargets");
        String content = request.getParameter("content");
        String fk_userid = request.getParameter("fk_userid");
        
        System.out.println("memberSeq=" + memberSeq);
        
        
        if (startDate == null || endDate == null) {
            mav.addObject("message", "시작일과 종료일을 입력하세요.");
            mav.addObject("loc", request.getContextPath() + "/calendar/addCalendarForm");
            mav.setViewName("msg");
            return mav;
        }

        // datetime-local input에서 나오는 yyyy-MM-ddTHH:mm 형식에서 T와 : 제거 후 뒤에 00 붙임 (초)
        String startDateTime = startDate.replace("T", "").replace(":", "") + "00";
        String endDateTime = endDate.replace("T", "").replace(":", "") + "00";

        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("startDateTime", startDateTime);
        paraMap.put("endDateTime", endDateTime);
        paraMap.put("title", title);
        paraMap.put("calendarType", calendarType);
        paraMap.put("shareTargets", shareTargets);
        paraMap.put("content", content);
        paraMap.put("memberSeq", fk_userid);

        paraMap.put("memberSeq", memberSeq);
        
        int n = service.addCalendarEvent(paraMap);

        if (n == 0) {
            mav.addObject("message", "일정 등록에 실패하였습니다.");
        } else {
            mav.addObject("message", "일정 등록에 성공하였습니다.");
        }

        // 등록 후 list.jsp로 이동 (URL 소문자)
        mav.addObject("loc", request.getContextPath() + "/Calendar/list");
        mav.setViewName("msg");

        return mav;
    }
}
