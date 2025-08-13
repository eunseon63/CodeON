package com.spring.app.board.model;

import com.spring.app.board.domain.BoardDTO;

public interface BoardDAO {
	
	// 파일첨부가 없는 글쓰기
	int add(BoardDTO boardDto);
	//글쓰기(파일첨부가 있는 글쓰기)
	int add_withFile(BoardDTO boardDto);
}
