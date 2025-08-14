package com.spring.app.board.model;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.BoardDTO;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class BoardDAO_imple implements BoardDAO {
	

	 private final SqlSession sqlSession;

	    @Override
	    public void insertBoard(BoardDTO boardDto) {
	        sqlSession.insert("board.insertBoard", boardDto);
	    }

		@Override
		public List<BoardDTO> selectBoardList(Map<String, String> paramMap) {
			 return sqlSession.selectList("board.selectBoardList", paramMap);
		}

	   

}
