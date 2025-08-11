package com.spring.app.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("CodeON/board") 
public class BoardController {

    @GetMapping("/list")
    public String boardList() {
        return "content/board/list"; 
    }
    
    @GetMapping("/add")
    public String boardRegister() {
    	return "content/board/add";
    }
}