package com.spring.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.entity.Member;
import com.spring.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member/")
@RequiredArgsConstructor  // final 필드 생성자 주입
public class MemberController {
	
	private final MemberService memberService;
	
	@GetMapping("register")
	public String memberRegister() {
		return "member/register";
	}

	@GetMapping("list")
	public String memberList() {
		return "member/list";
	}
	
    @GetMapping("sign/members")
    public List<Member> getMembers() {
        return memberService.getAllMembersOrderByDept();
    }
}
