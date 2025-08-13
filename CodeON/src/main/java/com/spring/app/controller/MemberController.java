package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member/")
@RequiredArgsConstructor  // final 필드 생성자 주입
public class MemberController {
	
	@GetMapping("register")
	public String memberRegister() {
		return "member/register";
	}

	@GetMapping("list")
	public String memberList() {
		return "member/list";
	}
}
