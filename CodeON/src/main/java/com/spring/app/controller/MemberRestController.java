package com.spring.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	// 검색 회원 조회
	@GetMapping("searchMember")
	public List<MemberDTO> searchMember(@RequestParam(name="searchType", defaultValue="")  String searchType,
 		   								@RequestParam(name="searchWord", defaultValue="")  String searchWord,
 		   								@RequestParam(name="gender", defaultValue="") String gender) {

		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("searchType", searchType);
		paraMap.put("searchWord", searchWord);
		paraMap.put("gender", gender);

		// ==> !!! 동적 조건으로 분기 처리해주는 QueryDSL 전용의 SQL 조건 표현 객체인 BooleanExpression 사용하여 처리하기 !!! <==
		return memberService.searchMember(paraMap);
	}
	
	// 회원 삭제
	@DeleteMapping("delete")
	public Map<String, Integer> delete(@RequestParam(name="memberSeq") String memberSeq) {
		
		int str_memberSeq = Integer.parseInt(memberSeq);
		
		int n = memberService.delete(str_memberSeq);
		
		Map<String, Integer> map = new HashMap<>();
		map.put("n", n);
		
		return map;
	}
	
}
