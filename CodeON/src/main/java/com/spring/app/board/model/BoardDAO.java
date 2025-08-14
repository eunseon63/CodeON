package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import com.spring.app.board.domain.BoardDTO;

public interface BoardDAO {
	
	void insertBoard(BoardDTO boardDto);

   

	List<BoardDTO> selectBoardList(Map<String, String> paramMap);
}
