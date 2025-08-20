package com.spring.app.service;

import java.util.List;

import com.spring.app.domain.SignlineDTO;

public interface SignlineService {
    
    // 로그인 사용자의 결재라인 목록 반환
	List<SignlineDTO> findAllByOwner(int memberSeq);
	
	List<SignlineDTO> getLinesWithMembers(Long fkMemberSeq);

}
