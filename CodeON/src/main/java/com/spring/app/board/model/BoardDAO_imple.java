package com.spring.app.board.model;


import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.spring.app.board.domain.BoardDTO;

import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class BoardDAO_imple implements BoardDAO {
	

	// 의존객체를 생성자 주입(DI : Dependency Injection)
	@Qualifier("sqlsession")
	private final SqlSessionTemplate sql;
	
		@Override
		public int add(BoardDTO boardDto) {
			int n = sql.insert("board.add", boardDto);
			return n;
		}
		
		//글쓰기(파일첨부가 있는 글쓰기)
		@Override
		public int add_withFile(BoardDTO boardDto) {
			int n =  sql.insert("board.add_withFile",boardDto);
			return n;
		}

}
