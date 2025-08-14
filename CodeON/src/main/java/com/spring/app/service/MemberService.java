package com.spring.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;

public interface MemberService {

	// 특정 회원 1명 읽어오기(로그인)
	public MemberDTO getMemberByUserId(String memberUserId);
	
	// 직원등록
	Member registerMember(Member member);

	public Page<Member> getPageMember(String searchType, String searchWord, String gender, int currentShowPageNo, int sizePerPage) throws Exception;

	// 회원 삭제
	public int delete(int memberSeq);
	
}

