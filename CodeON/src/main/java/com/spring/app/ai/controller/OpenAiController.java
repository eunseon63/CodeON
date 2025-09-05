package com.spring.app.ai.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.ai.service.OpenAiService;
import com.spring.app.domain.MemberDTO;
import com.spring.app.service.MemberService;

import jakarta.servlet.http.HttpSession;
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
	
    // DB 전체 인덱싱
    @GetMapping("index")
    public String indexDb() {
        openAiService.indexAllDbDocuments();
        return "DB 인덱싱 완료";
    }

    // RAG 챗봇 질문
    @GetMapping("chat")
    public String chat(@RequestParam(name="question") String question, HttpSession session) {
    	
    	MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

        return openAiService.ragChat(question, loginuser.getMemberSeq());
    }
}
