package com.spring.app.sign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor  
@RequestMapping(value="/sign/*")
public class SignController {

	@GetMapping("main")
	public String signmain() {
		return "/sign/signmain";
	}
	
	@GetMapping("setting")
	public String signsetting() {
		return "/sign/signsetting";
	}
	
}
