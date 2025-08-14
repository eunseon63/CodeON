package com.spring.app.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.model.BoardDAO;

import lombok.RequiredArgsConstructor;

// board service

@Service
@RequiredArgsConstructor  // @RequiredArgsConstructor는 Lombok 라이브러리에서 제공하는 애너테이션으로, final 필드 또는 @NonNull이 붙은 필드에 대해 생성자를 자동으로 생성해준다.
public class BoardService_imple implements BoardService {
	private final BoardDAO dao;
	
	 @Override
	    public void add(BoardDTO boardDto) throws Exception {
		 dao.insertBoard(boardDto);
	    }

	   

		@Override
		public List<BoardDTO> selectBoardList(Map<String, String> paramMap) {
			return dao.selectBoardList(paramMap);
		}

}
