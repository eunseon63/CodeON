package com.spring.app.calendar.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.calendar.domain.BigCategoryDTO;
import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.domain.SmallCategoryDTO;
import com.spring.app.calendar.service.CalendarService;
import com.spring.app.calendar.service.CategoryService;
import com.spring.app.common.MyUtil;
import com.spring.app.domain.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Calendar")
public class CalendarController {

    private final CalendarService service;
    private final CategoryService categoryService;

    @Autowired
    public CalendarController(CalendarService service, CategoryService categoryService) {
        this.service = service;
        this.categoryService = categoryService;
    }

    // === 일정 목록 페이지 ===
    @GetMapping("/list")
    public String calendarList() {
        return "Calendar/list";
    }

    // === 일정 등록 폼 ===
    @GetMapping("/addCalendarForm")
    public ModelAndView showAddCalendarForm(ModelAndView mav, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");

        if (loginUser == null) {
            mav.addObject("message", "로그인이 필요합니다.");
            mav.addObject("loc", request.getContextPath() + "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }

        List<BigCategoryDTO> bigCategoryList = categoryService.getAllBigCategories();
        List<SmallCategoryDTO> smallCategoryList = categoryService.getAllSmallCategories();

        mav.addObject("bigCategoryList", bigCategoryList);
        mav.addObject("smallCategoryList", smallCategoryList);
        mav.setViewName("Calendar/addCalendarForm");
        return mav;
    }

    // === 일정 등록 처리하기 === // 
    @PostMapping("/addCalendarForm")
    public ModelAndView addCalendarEvent(ModelAndView mav, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");

        if (loginUser == null) {
            mav.addObject("message", "로그인이 필요합니다.");
            mav.addObject("loc", request.getContextPath() + "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }

        try {
            // 파라미터 가져오기
            String memberSeq = String.valueOf(loginUser.getMemberSeq());
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String title = request.getParameter("title");
            String calendarType = request.getParameter("calendarType");
            String shareTargets = request.getParameter("shareEmployees");
            String content = request.getParameter("content");
            String bigCategorySeq = request.getParameter("bigCategorySeq");
            String smallCategorySeq = request.getParameter("smallCategorySeq");

            // 필수 체크
            if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
                throw new IllegalArgumentException("시작일과 종료일을 입력하세요.");
            }
            if (bigCategorySeq == null || bigCategorySeq.isEmpty()) {
                throw new IllegalArgumentException("대분류를 선택하세요.");
            }

            // String → Timestamp 변환
            Timestamp startTS = Timestamp.valueOf(startDate.replace("T", " ") + ":00");
            Timestamp endTS = Timestamp.valueOf(endDate.replace("T", " ") + ":00");

            // Map에 담기
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("memberSeq", memberSeq);
            paraMap.put("calendarStart", startTS);
            paraMap.put("calendarEnd", endTS);
            paraMap.put("title", title);
            paraMap.put("calendarType", calendarType);
            paraMap.put("shareTargets", shareTargets);
            paraMap.put("content", content);
            paraMap.put("bigCategorySeq", bigCategorySeq);
            paraMap.put("smallCategorySeq", smallCategorySeq);

            int n = service.addCalendarEvent(paraMap);

            mav.addObject("message", (n > 0) ? "일정 등록에 성공하였습니다." : "일정 등록에 실패하였습니다.");
            mav.addObject("loc", request.getContextPath() + "/Calendar/list");

        } catch (Exception e) {
            mav.addObject("message", "오류 발생: " + e.getMessage());
            mav.addObject("loc", request.getContextPath() + "/Calendar/addCalendarForm");
        }

        mav.setViewName("msg");
        return mav;
    }
    
   
    // 등록된 일정을 가져와 list.jsp 에 보여주기 //
    @ResponseBody
    @GetMapping(value="selectCalendar")
    public String selectCalendar(HttpServletRequest request) {
        // 요청 파라미터로 사번(calendar_user) 전달
        String calendarUser = request.getParameter("fk_userid");
        List<CalendarAjaxDTO> calendarList = service.selectCalendar(calendarUser);

        JSONArray jsArr = new JSONArray();

        if(calendarList != null && !calendarList.isEmpty()) {
            for(CalendarAjaxDTO svo : calendarList) {
                JSONObject jsObj = new JSONObject();
                jsObj.put("id", svo.getCalendarSeq());
                jsObj.put("title", svo.getCalendarName());
                jsObj.put("start", svo.getCalendarStart());
                jsObj.put("end", svo.getCalendarEnd());
                jsObj.put("color", svo.getCalendarColor());
                jsObj.put("type", svo.getCalendarType());
                jsObj.put("location", svo.getCalendarLocation());
                jsObj.put("content", svo.getCalendarContent());
                jsObj.put("user", svo.getCalendarUser());
                jsArr.put(jsObj);
            }
        }

        System.out.println("calendarUser=" + calendarUser);
        
        return jsArr.toString();
    }

    // === 일정 상세보기 ===
    @GetMapping(value="detailCalendar")
    public ModelAndView detailCalendar(ModelAndView mav, HttpServletRequest request) {
        
        // 1. 요청 파라미터
    	String calendarSeqStr = request.getParameter("calendarSeq");

        // 2. 이전 페이지로 돌아가기용 URL (필요 시)
        String listGoBackURL = request.getParameter("listGoBackURL");
        mav.addObject("listGoBackURL", listGoBackURL);

        // 3. 현재 URL 저장 (상세보기에서 수정페이지 이동 시 필요)
        String goBackURL = MyUtil.getCurrentURL(request);
        mav.addObject("goBackURL", goBackURL);

        try {
            int calendarSeq = Integer.parseInt(calendarSeqStr); // String → int

            Map<String, String> map = service.detailCalendar(calendarSeq);
            mav.addObject("map", map);

            mav.setViewName("Calendar/detailCalendar");
        } catch (NumberFormatException e) {
            mav.setViewName("redirect:/calendar/calendarList");
        }

        return mav;
    }

   






    
    
    
    
    
    
    
    
    
    
    
}
