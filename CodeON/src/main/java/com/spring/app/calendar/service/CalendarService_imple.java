package com.spring.app.calendar.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.calendar.domain.CalendarAjaxDTO;
import com.spring.app.calendar.model.CalendarDAO;


@Service
public class CalendarService_imple implements CalendarService {


	    private final CalendarDAO dao;

	    // 생성자 주입
	    public CalendarService_imple(CalendarDAO dao) {
	        this.dao = dao;
	    }

	    //////////////////////////////////////////////////////////////
	    
	    
	    // === 일정 등록 처리 ===
	    @Override
	    public int addCalendarEvent(Map<String, Object> paraMap) {
	    	return dao.addCalendarEvent(paraMap); 
	    	
	    }

	    // 등록된 캘린더를 화면에 보여주는 거 만들자~ //
		@Override
		public List<CalendarAjaxDTO> selectCalendar(String fk_userid) {
			List<CalendarAjaxDTO> calendarList = dao.selectCalendar(fk_userid);
			return calendarList;
		}



		
		


}
	


