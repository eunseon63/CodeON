package com.spring.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.common.FileManager;
import com.spring.app.domain.MemberDTO;
import com.spring.app.model.MemberRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor  
@RequestMapping(value="/sign/*")
public class SignController {

	private final FileManager fileManager;
	private final MemberRepository memberRepository;
	
	@GetMapping("main")
	public String signmain() {
		return "/sign/signmain";
	}
	
	@GetMapping("setting")
	public String signsetting() {
		return "/sign/signsetting";
	}
	
	@PostMapping("stampImageSave")
	public void stampImageSave(HttpServletRequest request, HttpServletResponse response) {
	    HttpSession session = request.getSession();
	    String root = session.getServletContext().getRealPath("/");
	    String path = root + "resources" + File.separator + "stamp_upload";

	    File dir = new File(path);
	    if (!dir.exists()) {
	        dir.mkdirs();
	    }

	    try {
	        String filename = request.getHeader("file-name"); // 원본 파일명
	        InputStream is = request.getInputStream();

	        // 파일 저장 (fileManager는 직접 구현하거나 기존 거 재사용)
	        String newFilename = fileManager.doFileUpload(is, filename, path);
	        
	        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
	        memberRepository.stampImageSave(loginuser.getMemberUserid(), newFilename);

	        String ctxPath = request.getContextPath();
	        String fileUrl = ctxPath + "/resources/stamp_upload/" + newFilename;

	        // JSON 응답
	        response.setContentType("application/json;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        out.print("{\"result\":\"success\", \"url\":\"" + fileUrl + "\"}");
	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            response.setContentType("application/json;charset=UTF-8");
	            response.getWriter().print("{\"result\":\"fail\"}");
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	    }
	}

	
}
