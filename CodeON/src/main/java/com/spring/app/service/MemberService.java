package com.spring.app.service;

import java.util.List;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;

public interface MemberService {

	// 직원등록
	Member registerMember(Member member);

	List<MemberDTO> getAllMember();

}
