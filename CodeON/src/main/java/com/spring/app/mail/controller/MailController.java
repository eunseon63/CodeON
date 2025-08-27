package com.spring.app.mail.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.mail.service.MailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mail/")
@RequiredArgsConstructor
public class MailController {
	
	private final MailService service;
	
	@GetMapping("list")
	public String list() {
		return "mail/list";
	}
	
	@GetMapping("send")
	public String send() {
		return "mail/send";
	}
	
	@GetMapping("important")
	public String important() {
		return "mail/important";
	}
	
	@GetMapping("write")
	public String write() {
		return "mail/write";
	}
	

}
