package com.spring.app.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.board.domain.CommentDTO;
import com.spring.app.board.domain.ReplyDTO;

@Service
public interface CommentService {
	

//댓글
	   List<CommentDTO> getCommentList(int fkBoardSeq); // 댓글 조회


	    int addComment(Map<String, Object> paramMap);
	    int deleteComment(Integer commentSeq, int memberSeq);
//대댓글
		int addReply(Map<String, Object> paramMap);


		List<ReplyDTO> getReplyList(Integer parentCommentSeq);

		

		int deleteReply(Integer replySeq, int memberSeq);


		int editComment(CommentDTO cdto);


		int editReply(ReplyDTO rdto);


		


	
		 
	
	 
	 
	 
	 
	 
}
