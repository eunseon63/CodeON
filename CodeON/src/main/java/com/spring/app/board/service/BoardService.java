package com.spring.app.board.service;

import com.spring.app.board.domain.BoardDTO;

import java.util.List;
import java.util.Map;

public interface BoardService {
    void add(BoardDTO boardDto) throws Exception;

   
	List<BoardDTO> selectBoardList(Map<String, String> paramMap);
}
