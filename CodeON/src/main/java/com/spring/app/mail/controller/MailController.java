package com.spring.app.mail.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.common.FileManager;
import com.spring.app.common.MyUtil;
import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.service.MailService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mail/")
@RequiredArgsConstructor
public class MailController {
	
	private final MailService service;
	private final FileManager fileManager;
	
	@GetMapping("list")
	public ModelAndView list(ModelAndView mav, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
			@RequestParam(name="currentShowPageNo", defaultValue = "1") String currentShowPageNo) {

		List<MailDTO> mailList = null;
		
		/* ======================================================== 
		글조회수(readCount)증가 (DML문 update)는 
		반드시 목록보기에 와서 해당 글제목을 클릭했을 경우에만 증가되고,
		웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 되지 않도록 해야 한다. 
		이것을 하기 위해서는 session 을 사용하여 처리하면 된다.
		=========================================================
		*/
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		/*
		session 에  "readCountPermission" 키값으로 저장된 value값은 "yes" 이다.
		session 에  "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 
		반드시 웹브라우저에서 주소창에 "/board/list" 이라고 입력해야만 얻어올 수 있다. 
		*/
		
		
		// ==== 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 ==== //
		//boardList = service.boardListNoSearch();
		
		// ==== 페이징 처리를 "안한" 검색어가 있는 전체 글목록 보여주기 ==== //
		// GET 방식인데 검색어가 있으므로 사용자가 URL 주소에 검색어를 직접 입력하여 장난칠 수 있으므로 이것을 방지하고자 Referer 를 사용하도록 한다.
		String referer = request.getHeader("Referer");
		if (referer == null) {
			mav.setViewName("redirect:/index");
			return mav;
		}
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchWord", searchWord);
		
		// 페이징 처리를 안한것
		//boardList = service.boardListSearch(paraMap); // <== 페이징 처리를 "안한" 검색어가 있는 전체 글목록 보여주기
		
		// ==== 페이징 처리를 "한" 검색어가 없는 전체 글목록 보여주기 ==== //
		
		// 먼저, 총 게시물 건수(totalCount)를 구해와야 한다.
		// 총 게시물 건수(totalCount)는 검색조건이 있을 때와 검색조건이 없을때로 나뉘어진다.
		int totalCount = 0; // 총 게시물 건수
		int sizePerPage = 10; // 한 페이지당 보여줄 게시물 건수
		int totalPage = 0; // 총 페이지수(웹브라우저상에서 보여줄 총 페이지 개수, 페이지바)
		
		// 총 게시물 건수(totalCount)
		totalCount = service.getTotalCount(paraMap);
		// System.out.println("~~~ 확인용 totalCount : " + totalCount);
		
		// 만약에 총 게시물 건수(totalCount)가 124 개 이라면 총 페이지수(totalPage)는 13 페이지가 되어야 한다.
		// 만약에 총 게시물 건수(totalCount)가 120 개 이라면 총 페이지수(totalPage)는 12 페이지가 되어야 한다. 
		totalPage = (int) Math.ceil((double) totalCount/sizePerPage);
		// (double) 124/10 ==> 12.4 ==> Math.ceil(12.0) ==> 13.0 ==> 13
		// (double) 120/10 ==> 12.0 ==> Math.ceil(12.0) ==> 12.0 ==> 12
		
		paraMap.put("currentShowPageNo", currentShowPageNo); // Oracle 12c 이상으로 사용하는 것.
		
		// 글목록 가져오기(페이징 처리했으먀, 검색어가 있는 것 또는 검색어가 없는 것 모두 포함한 것이다.
		mailList = service.mailListSearch_withPaging(paraMap); // <== 페이징 처리를 "한" 검색어가 있는 전체 글목록 보여주기
		
		mav.addObject("mailList", mailList);
		
		// === 페이지바 만들기 시작 === //
		int blockSize = 10;
		// blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수이다.
		/*
		      1  2  3  4  5  6  7  8  9 10 [다음][마지막]  -- 1개블럭
		[맨처음][이전]  11 12 13 14 15 16 17 18 19 20 [다음][마지막]  -- 1개블럭
		[맨처음][이전]  21 22 23
		*/
		int loop = 1;
		/*
		loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
		*/
		
		int pageNo = ((Integer.parseInt(currentShowPageNo) - 1)/blockSize) * blockSize + 1;
		// *** !! 공식이다. !! *** //
		
		/*
		1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
		11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
		21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
		
		currentShowPageNo         pageNo
		----------------------------------
		  1                      1 = ((1 - 1)/10) * 10 + 1
		  2                      1 = ((2 - 1)/10) * 10 + 1
		  3                      1 = ((3 - 1)/10) * 10 + 1
		  4                      1
		  5                      1
		  6                      1
		  7                      1 
		  8                      1
		  9                      1
		  10                     1 = ((10 - 1)/10) * 10 + 1
		 
		  11                    11 = ((11 - 1)/10) * 10 + 1
		  12                    11 = ((12 - 1)/10) * 10 + 1
		  13                    11 = ((13 - 1)/10) * 10 + 1
		  14                    11
		  15                    11
		  16                    11
		  17                    11
		  18                    11 
		  19                    11 
		  20                    11 = ((20 - 1)/10) * 10 + 1
		  
		  21                    21 = ((21 - 1)/10) * 10 + 1
		  22                    21 = ((22 - 1)/10) * 10 + 1
		  23                    21 = ((23 - 1)/10) * 10 + 1
		  ..                    ..
		  29                    21
		  30                    21 = ((30 - 1)/10) * 10 + 1
		*/
		
		String pageBar = "<ul style='list-style:none;'>";
		String url = "list";
		
		// === [맨처음][이전] 만들기 === // 
		pageBar += "<li style='display: inline-block; width: 70px; font-size: 12px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=1'>[맨처음]</a></li>";
		
		if (pageNo != 1) {
			pageBar += "<li style='display: inline-block; width: 50px; font-size: 12px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + (pageNo-1) + "'>[이전]</a></li>";	
		}
		
		while(!(loop > blockSize || pageNo > totalPage)) {
			
			if (pageNo == Integer.parseInt(currentShowPageNo)) {
				pageBar += "<li style='display: inline-block; width: 30px; font-size: 12px; border: solid 1px gray; color: red; padding: 2px 4px;'>" + pageNo + "</li>";
			} else {
				pageBar += "<li style='display: inline-block; width: 30px; font-size: 12px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + pageNo + "'>" + pageNo + "</a></li>";
			}
			
			loop++;
			pageNo++;
		} // end of while()--------
		
		// === [다음][마지막] 만들기 === // 
		if (pageNo <= totalPage) {
			pageBar += "<li style='display: inline-block; width: 50px; font-size: 12px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + pageNo + "'>[다음]</a></li>";			
		}
		pageBar += "<li style='display: inline-block; width: 70px; font-size: 12px;'><a href='" + url + "?searchWord=" + searchWord + "&currentShowPageNo=" + totalPage + "'>[마지막]</a></li>";			
		
		pageBar += "</ul>";
		
		mav.addObject("pageBar",pageBar);
		// === 페이지바 만들기 끝 === //
		
		// =============== 페이징 처리시 보여주는 순번을 나타내기 위한 것임. =============== //
		mav.addObject("totalCount", totalCount);
		mav.addObject("currentShowPageNo", currentShowPageNo);
		mav.addObject("sizePerPage", sizePerPage);
		// ==================================================================== //
		
		// 페이징 처리되어진 후 특정 글제목을 클릭하여 상세내용을 본 이후
		// 사용자가 "검색된결과목록보기" 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
		// 현재 페이지 URL 주소를 쿠키에 저장한다.
		String listURL = MyUtil.getCurrentURL(request);
		//	System.out.println("~~~ 확인용 listURL : " + listURL);
		// ~~~ 확인용 listURL : /board/list
		// ~~~ 확인용 listURL : /board/list?searchType=&searchWord=&currentShowPageNo=7
		// ~~~ 확인용 listURL : /board/list?searchType=subject&searchWord=입니다&currentShowPageNo=7
		
		Cookie cookie = new Cookie("listURL", listURL);
		// new Cookie(쿠키명, 쿠키값); 
		// Cookie 클래스 임포트시 jakarta.servlet.http.Cookie 임.
		
		cookie.setMaxAge(24*60*60); // 쿠키수명은 1일로 함.
		cookie.setPath("/mail/"); // 쿠키가 브라우저에서 전송될 URL 경로 범위(Path)를 지정하는 설정임.
		/* 
		Path를 /myspring/board/ 로 설정하면:
		/myspring/board/view_2, /myspring/board/view 등 /myspring/board/ 로 시작하는 경로에서만 쿠키가 전송된다. 
		/myspring/, /myspring/index, /myspring/login 등의 다른 경로에서는 이 쿠키는 사용되지 않음.   
		*/
		response.addCookie(cookie); // 접속한 클라이언트 PC로 쿠키를 보내줌
		
		mav.setViewName("/mail/list");
		//  /WEB-INF/views/mycontent1/board/list.jsp 파일을 만들어야 한다.
		
		return mav;
	}
	
	@GetMapping("send")
	public String send() {
		return "mail/send";
	}
	
	@GetMapping("important")
	public String important() {
		return "mail/important";
	}
	
	@GetMapping("write")
	public ModelAndView write(HttpServletRequest request,
					          HttpServletResponse response,
					          ModelAndView mav) {
		
		mav.setViewName("mail/write");
		return mav;
	}
	

	
	@PostMapping("write")
	public ModelAndView write(ModelAndView mav, MailDTO mailDto, HttpServletRequest request) {

		MultipartFile attach = mailDto.getAttach();

		// === 사용자(클라이언트)가 쓴 글에 파일이 첨부되어 있으면 클라이언트가 올리려고 하는 첨부파일을 가져와서 WAS 의 disk 상에 파일을 올려주어야 한다.
		if (!attach.isEmpty()) {
			// attach(첨부파일)이 있으면
			/*
	            1. 사용자가 보낸 첨부파일을 WAS(톰캣)의 특정 폴더에 저장해주어야 한다.
	            >>> 파일이 업로드 되어질 특정 경로(폴더)지정해주기 
	                우리는 WAS 의 /myspring/src/main/webapp/resources/files 라는 폴더를 생성해서 여기로 업로드 해주도록 할 것이다. 
            */
			
			// WAS의 webapp 의 절대경로를 알아와야 한다
			HttpSession session = request.getSession();
			String root = session.getServletContext().getRealPath("/");
			
			// System.out.println(root);
			// C:\NCS\worksapce_spring_boot_17\myspring\src\main\webapp\
			
			String path = root + "resources" + File.separator + "files";
			// path 가 첨부파일이 저장될 WAS(톰캣)의 폴더가 된다.
		    // System.out.println("~~~ 확인용 path ==> " + path);
		    // ~~~ 확인용 path ==> C:\NCS\worksapce_spring_boot_17\myspring\src\main\webapp\resources\files
			
			/*
            	2. 파일첨부를 위한 변수의 설정 및 값을 초기화 한 후 파일 올리기
			*/
			
			System.out.println("path");
	        String newFileName = "";
	        // WAS(톰캣)의 디스크에 저장될 파일명
	         
	        byte[] bytes = null;
	        // 첨부파일의 내용물을 담는 것
	         
	        long fileSize = 0;
	        // 첨부파일의 크기
	        
	        try {
	        	bytes = attach.getBytes();
				// 첨부파일의 내용물을 읽어오는 것
				
				String originalFilename = attach.getOriginalFilename();
				// attach.getOriginalFilename() 이 첨부파일명의 파일명(예: 강아지.png) 이다. 
				
				// System.out.println("~~~ 확인용 originalFilename => " + originalFilename);
				// ~~~ 확인용 originalFilename => berkelekle단가라포인트03.jpg
				
				// 첨부되어진 파일을 업로드 하는 것이다.
				newFileName = fileManager.doFileUpload(bytes, originalFilename, path);
				
				// System.out.println(newFileName);
				// 20250725123914_9e2962fb90f0410aa5a3781fb444d4aa.jpg
				
				// BoardDto boardDto 에 fileName 값과 orgFilename 값과 fileSize 값을 넣어주기
				mailDto.setEmailFilename(newFileName);
				// WAS의 disk 상에 저장된 파일명(20250725123914_9e2962fb90f0410aa5a3781fb444d4aa.jpg)
				
				mailDto.setEmailOrgFilename(originalFilename);
				// 게시판 페이지에서 첨부된 파일(강아지.png)을 보여줄 때 사용.
	            // 또한 사용자가 파일을 다운로드 할때 사용되어지는 파일명으로 사용.
				
				fileSize = attach.getSize(); // 첨부파일의 크기(단위는 byte임)
				mailDto.setEmailFilesize(String.valueOf(fileSize));
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	        
		}
		
		int n = 0;
		
		if (attach.isEmpty()) {
			// 첨부파일이 없는 경우라면
			// System.out.println("~~~ 확인용 : 첨부파일이 없군요!!");
			n = service.write(mailDto); // <== 파일첨부가 없는 글쓰기
		} else {
			// 첨부파일이 있는 경우라면
			// System.out.println("~~~ 확인용 : 첨부파일이 있군요!!");
			n = service.write_withFile(mailDto); // <== 파일첨부가 있는 글쓰기 
		}
		
		if(n==1) {
			mav.setViewName("redirect:/mail/list");
		}
		
		return mav;
		
	}
	
	

}
