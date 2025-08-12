package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/member/")
@RequiredArgsConstructor  // final 필드 생성자 주입
public class AttendanceController {

    // 근태 관리 메인 페이지 
    @GetMapping("work")
    public String workPage(Model model, HttpSession session) {
        return "member/work";
    }

 
}
