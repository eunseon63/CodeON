package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontController {
	
	@GetMapping("")
	public String start() {
		return "index";
	}

	@GetMapping("member/register")
	public String memberRegister() {
		return "member/register";
	}
	
	@GetMapping("member/list")
	public String memberList() {
		return "member/list";
	}
}
