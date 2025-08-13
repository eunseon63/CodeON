package com.spring.app.board.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;
import com.spring.app.board.service.BoardService_imple;
import com.spring.app.common.FileManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor  // @RequiredArgsConstructor는 Lombok 라이브러리에서 제공하는 애너테이션으로, final 필드 또는 @NonNull이 붙은 필드에 대해 생성자를 자동으로 생성해준다.
@RequestMapping("/board/") 
public class BoardController {

	private final BoardService service;
	private final FileManager fileManager;

   
    
    //게시글 작성 폼페이지 요청
    @GetMapping("add")
    public ModelAndView boardRegister(HttpServletRequest request,
    									HttpServletResponse response,
    									ModelAndView mav) {
    	String boardSeq = request.getParameter("boardSeq");
    	
    	
    	
    	mav.setViewName("content/board/add");
    	return mav;
    }
  //게시글 작성 폼페이지 제출
    
    @PostMapping("add")
    public ModelAndView boardRegister_post(BoardDTO boardDto) {
    	ModelAndView mav= new ModelAndView();
    	int n = 0;
    	
    	n = service.add(boardDto);
    	
    	if(n==1) {
    		mav.setViewName("redirect:/board/list");
    	}else {
    		mav.setViewName("content/board/add");
    		mav.addObject("msg","글 작성 실패");
    		
    	}
    	
    	return mav;
    }
    
	//게시글 목록 요청
    @GetMapping("list")
    public String boardList() {
        return "content/board/list"; 
    }
}	