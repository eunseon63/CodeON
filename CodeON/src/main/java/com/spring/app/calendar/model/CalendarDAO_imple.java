package com.spring.app.calendar.model;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.calendar.domain.CalendarAjaxDTO;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class CalendarDAO_imple implements CalendarDAO {

	
	@Qualifier("sqlsession")
	private final SqlSessionTemplate sqlsession;

	// 등록 이벤트
	@Override
	public int addCalendarEvent(Map<String, Object> paraMap) {
		
			int n = sqlsession.insert("calendar.addCalendarEvent", paraMap); 
		
		return n; 
	}

	// 등록된 캘린더를 화면에 보여주는 거 만들자~ //
	@Override
	public List<CalendarAjaxDTO> selectCalendar(String fk_userid) {
		List<CalendarAjaxDTO> calendarList = sqlsession.selectList("calendar.selectCalendar", fk_userid);
		return calendarList;
	}

	
	


	




	
}
