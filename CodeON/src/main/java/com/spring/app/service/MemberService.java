package com.spring.app.service;

import java.util.List;
import java.util.Map;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;

public interface MemberService {

	// 특정 회원 1명 읽어오기(로그인)
	public MemberDTO getMemberByUserId(String memberUserId);
	
	// 직원등록
	Member registerMember(Member member);

	// 모든 회원 조회
	List<MemberDTO> getAllMember();

	// 검색 회원 조회
	public List<MemberDTO> searchMember(Map<String, String> paraMap);
	
}

