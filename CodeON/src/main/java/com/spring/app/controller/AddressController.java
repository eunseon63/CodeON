package com.spring.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.spring.app.domain.MemberDTO;
import com.spring.app.service.AddressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;
    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(value="dept", required=false) Integer dept,
                       @RequestParam(value="q",    required=false) String kw,
                       @RequestParam(value="page", required=false, defaultValue="1") int page,
                       Model model) {

        var departments = addressService.departments();
        var result = addressService.search(dept, kw, page, PAGE_SIZE);

        model.addAttribute("departments", departments);
        model.addAttribute("selectedDept", dept);	
        model.addAttribute("keyword", kw);	

        model.addAttribute("items", result.getContent());
        model.addAttribute("page", result.getNumber() + 1); // 1-based
        model.addAttribute("totalPages", result.getTotalPages());

        return "address/list"; // 기존 JSP 그대로
    }
}
