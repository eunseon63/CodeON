package com.spring.app.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



		@Override
		public List<Map<String, Object>> getBoardTypeList() {
			  return dao.getBoardTypeList();
		}



		@Override
		public List<Map<String, Object>> getBoardCategoryList() {
			return dao.getBoardCategoryList();
		}



		@Override
		public int getTotalCount(Map<String, Object> paraMap) {
			int totalCount = dao.getTotalCount(paraMap);
			return totalCount;
		}



		@Override
		public List<BoardDTO> boardListSearch_withPaging(Map<String, Object> paraMap) {
			List<BoardDTO> boardList= dao.boardListSearch_withPaging(paraMap);
			return boardList;
		}



		@Override
		public BoardDTO getBoardDetail(String boardSeq) {
			dao.updateReadCount(boardSeq);   //조회수 증가
			 return dao.getBoardDetail(boardSeq);
		}


		@Override
		public BoardDTO getPrevBoard(Map<String, Object> paraMap) {
			return dao.getPrevBoard(paraMap);
		}



		@Override
		public BoardDTO getNextBoard(Map<String, Object> paraMap) {
			return dao.getNextBoard(paraMap);
		}




		@Override
		@Transactional  // 글 삭제시 첨부파일,댓글 까지 함께 처리하는 트랜잭션 , tbl_comment 테이블에는 시퀀스에 delete cascade 제약조건 걸려있음
		public int delete(String boardSeq) {
			   return dao.delete(boardSeq);
		}



		@Override
		public void updateBoard(BoardDTO boardDto) throws Exception {
			dao.updateBoard(boardDto);
			
		}


		// 메인 화면
		@Override
		public List<BoardDTO> selectRecentNotices(List<Integer> typeSeqs, int limit){
		    Map<String,Object> p = new HashMap<>();
		    p.put("typeSeqs", typeSeqs);      // 예: [0, 1]
		    p.put("limit", limit);            // 예: 5
		    return dao.selectRecentNoticesFromTypes(p);
		}



		@Override
	    public String getDepartmentNameBySeq(Integer fkDepartmentSeq) {
	        if (fkDepartmentSeq == null) return null;
	        return dao.getDepartmentNameBySeq(fkDepartmentSeq);
	    }



	

		





}