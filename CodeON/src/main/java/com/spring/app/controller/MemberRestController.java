package com.spring.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;
import com.spring.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/memberInfo/")
@RequiredArgsConstructor
public class MemberRestController {
	
	private final MemberService memberService;
	
	// 직원등록
	@PostMapping("register")
	public Map<String, Member> register(MemberDTO mbrDto) {
		
		Member member = Member.builder()
							  .memberName(mbrDto.getMemberName())
							  .memberSeq(mbrDto.getMemberSeq())
							  .memberUserid(mbrDto.getMemberUserid())
							  .memberPwd(mbrDto.getMemberPwd())
							  .memberMobile(mbrDto.getMemberMobile())
							  .memberEmail(mbrDto.getMemberEmail())
							  .fkGradeSeq(mbrDto.getFkGradeSeq())
							  .fkDepartmentSeq(mbrDto.getFkDepartmentSeq())
							  .memberHiredate(mbrDto.getMemberHiredate())
							  .build();
		
		Member mbr = memberService.registerMember(member);
		
		Map<String, Member> map = new HashMap<>();
		map.put("member", mbr);
		
		return map;
	}
}
