package com.spring.app.calendar.service;

import java.util.Map;

public interface CalendarService {

	// === 일정 등록 처리 ===
	int addCalendarEvent(Map<String, String> paraMap);



}
