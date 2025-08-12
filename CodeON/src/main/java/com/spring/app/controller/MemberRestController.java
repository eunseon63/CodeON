package com.spring.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;
import com.spring.app.model.MemberRepository;
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
							  .memberUserid(mbrDto.getMemberUserid())
							  .memberPwd(mbrDto.getMemberPwd())
							  .memberEmail(mbrDto.getMemberEmail())
							  .memberMobile(mbrDto.getMemberMobile())
							  .fkGradeSeq(mbrDto.getFkGradeSeq())
							  .fkDepartmentSeq(mbrDto.getFkDepartmentSeq())
							  .memberBirthday(mbrDto.getMemberBirthday())
							  .memberHiredate(mbrDto.getMemberHiredate())
							  .memberGender(mbrDto.getMemberGender())
							  .build();
		
		Member mbr = memberService.registerMember(member);
		
		Map<String, Member> map = new HashMap<>();
		map.put("member", mbr);
		
		return map;
	}
	
	// 모든 회원 조회
	@GetMapping("allMember")
	public List<MemberDTO> allMember() {
		return memberService.getAllMember();
	}
	
	
}
