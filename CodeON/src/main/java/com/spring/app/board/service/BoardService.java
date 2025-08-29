package com.spring.app.board.service;

import com.spring.app.board.domain.BoardDTO;

import java.util.List;
import java.util.Map;

public interface BoardService {
    void add(BoardDTO boardDto) throws Exception;

   
	List<BoardDTO> selectBoardList(Map<String, String> paramMap);


	List<Map<String, Object>> getBoardTypeList();


	List<Map<String, Object>> getBoardCategoryList();


	int getTotalCount(Map<String, String> paraMap);

	
	List<BoardDTO> boardListSearch_withPaging(Map<String, String> paraMap);


	BoardDTO getBoardDetail(String boardSeq);


	BoardDTO getPrevBoard(String boardSeq);


	BoardDTO getNextBoard(String boardSeq);

	//글 삭제
	 int delete(String boardSeq);


	 void updateBoard(BoardDTO boardDto)throws Exception;


	 String getDepartmentNameBySeq(Integer userDept);

	//공지사항 가져오기
	 List<BoardDTO> getRecentNotices(Map<String, String> noticeMap); 	
	
	

}
