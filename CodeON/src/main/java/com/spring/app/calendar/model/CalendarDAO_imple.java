package com.spring.app.calendar.model;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class CalendarDAO_imple implements CalendarDAO {

	@Qualifier("sqlsession")
	private final SqlSessionTemplate sqlsession;
	
	@Override
	public int addCalendarEvent(Map<String, String> paraMap) {
		int n = sqlsession.insert("calendar.addCalendarEvent", paraMap);
		return n;
	}

}
