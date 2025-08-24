package com.spring.app.common;

import jakarta.servlet.http.HttpServletRequest;

public class MyUtil {
	
	// *** ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성 *** //
	public static String getCurrentURL(HttpServletRequest request) { 
		
		String currentURL = request.getRequestURL().toString();
		// System.out.println("확인용 currentURL : " + currentURL);
		// 확인용 currentURL : http://localhost:9090/myspring/board/add
		
		String queryString = request.getQueryString();
		// System.out.println("확인용 queryString : " + queryString);
		// 확인용 queryString : name=superman&age=30
		// 확인용 queryString : null (POST 방식일 경우)
		
		if (queryString != null) { // GET 방식일 경우
			currentURL += "?" + queryString;
			// currentURL => http://localhost:9090/myspring/board/add?name=superman&age=30
		}
		
		String ctxPath = request.getContextPath();
		// /myspring
		
		int beginIndex = currentURL.indexOf(ctxPath) + ctxPath.length(); // 21 + 9 => 30
		
		currentURL = currentURL.substring(beginIndex);
		// System.out.println("currentURL = > " + currentURL);
		// currentURL = > /board/add?name=superman&age=30
		
		return currentURL;
	}
}
