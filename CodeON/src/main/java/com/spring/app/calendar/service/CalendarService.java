package com.spring.app.calendar.service;

import java.util.List;
import java.util.Map;

import com.spring.app.calendar.domain.CalendarAjaxDTO;

public interface CalendarService {

	// === 일정 등록 처리 ===
	int addCalendarEvent(Map<String, Object> paraMap);

	// 등록된 캘린더를 화면에 보여주는 거 만들자~ //
	List<CalendarAjaxDTO> selectCalendar(String fk_userid);

	// === 일정 상세보기 ===
	// Map<String, String> detailCalendar(String calendarSeq);

	// === 일정 상세보기 ===
	Map<String, String> detailCalendar(int calendarSeq);

	




}
