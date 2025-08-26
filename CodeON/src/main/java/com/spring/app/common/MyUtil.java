package com.spring.app.common;

import jakarta.servlet.http.HttpServletRequest;

public class MyUtil {
	
	// *** ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성 *** //
	   public static String getCurrentURL(HttpServletRequest request) { 
		   
		   
		   
		   String currentURL = request.getRequestURL().toString();
		  
		   
		   String queryString = request.getQueryString();
		   System.out.println("확인용 queryString : "+queryString);
		   // 확인용 queryString : name=superman&age =30
		  if(queryString != null) {
			  currentURL += "?" +queryString;
			  
		  }
		   
		String ctxPath = request.getContextPath();
		  
		int beginIndex = currentURL.indexOf(ctxPath)+ ctxPath.length();
			//	30		= 	   21  +  9
			
		currentURL = currentURL.substring(beginIndex);
		System.out.println("currentURL =>" + currentURL);
		/*
		 
		 */
		
		   return currentURL;
	   }
}
