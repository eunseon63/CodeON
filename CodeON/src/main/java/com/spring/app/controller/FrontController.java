package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontController {

	@GetMapping("")
	public String start() {
		return "redirect:/login/loginStart";
	}
	
	@GetMapping("index")
	public String index() {
		return "index";
		//   /WEB-INF/views/index.jsp 파일을 만들어야 한다.
	}
	
	@GetMapping("admin")
	public String admin() {
		return "admin/register";
		//   /WEB-INF/views/admin/register.jsp 파일을 만들어야 한다.
	}
	
	@GetMapping("list")
	public String list() {
		return "admin/list";
		//   /WEB-INF/views/admin/list.jsp 파일을 만들어야 한다.
	}

}
