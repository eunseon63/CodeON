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
	
	@GetMapping("Calendar/list")
	public String calendarList() {
		return "Calendar/list";
	}
	
	
}
