package com.spring.app.ai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.ai.service.OpenAiService;
import com.spring.app.domain.MemberDTO;
import com.spring.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai/")
@RequiredArgsConstructor
public class OpenAiController {

	private final OpenAiService openAiService;
	private final MemberService memberService;
	
	@GetMapping("memberChat")
	public String memberChat() {
		List<MemberDTO> members = memberService.findAll();
		
		return openAiService.memberChat(members);
	}
	
}
