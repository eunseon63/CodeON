package com.spring.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

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

	// 직원 찾기
	public MemberDTO getMemberOne(String memberSeq);

	// 직원 수정
	public Member updateMember(Member member);
	
	// 검색 회원 조회
	public List<MemberDTO> searchMember(Map<String, String> paraMap);
	
	// 부서별 회원
	public List<Member> getAllMembersOrderByDept();

	// 결재라인에 추가할 수 있는 직원(사원 제외 전부)
	public List<MemberDTO> getSignlineMember();
}

