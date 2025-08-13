package com.spring.app.board.service;

import org.springframework.stereotype.Service;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.model.BoardDAO;

import lombok.RequiredArgsConstructor;

// board service

@Service
@RequiredArgsConstructor  // @RequiredArgsConstructor는 Lombok 라이브러리에서 제공하는 애너테이션으로, final 필드 또는 @NonNull이 붙은 필드에 대해 생성자를 자동으로 생성해준다.
public class BoardService_imple implements BoardService {
	private final BoardDAO dao;
	// 파일첨부가 없는 글쓰기
	@Override
	public int add(BoardDTO boardDto) {
		int n = dao.add(boardDto);
		return n;
	}
	
	 //글쓰기(파일첨부가 있는 글쓰기)
	@Override
	public int add_withFile(BoardDTO boardDto) {
		
		int n = dao.add_withFile(boardDto);
		return n;
	}

}
