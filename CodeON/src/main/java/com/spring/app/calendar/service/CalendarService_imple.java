package com.spring.app.calendar.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.calendar.model.CalendarDAO;

@Service
public class CalendarService_imple implements CalendarService {


	    private final CalendarDAO dao;

	    // 생성자 주입
	    public CalendarService_imple(CalendarDAO dao) {
	        this.dao = dao;
	    }
	 
	    // === 일정 등록 처리 ===
	    @Override
	    public int addCalendarEvent(Map<String, String> paraMap) {
	        return dao.addCalendarEvent(paraMap);
	    }
	}


