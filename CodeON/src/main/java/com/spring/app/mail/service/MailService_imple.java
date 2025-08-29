package com.spring.app.mail.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.model.MailDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService_imple implements MailService {
	
	private final MailDAO dao;

	
	// 파일첨부가 없는 글쓰기
	@Override
	public int write(MailDTO mailDto) {
		int n = dao.write(mailDto);
		return n;
	}

	// 파일첨부가 있는 글쓰기 
	@Override
	public int write_withFile(MailDTO mailDto) {
		int n = dao.write_withFile(mailDto);
		return n;
	}

	// 총 메일 (totalCount) 구하기
	@Override
	public int getTotalCount(Map<String, String> paraMap) {
		int totalCount = dao.getTotalCount(paraMap);
		return totalCount;
	}

	// 메일목록 가져오기
	@Override
	public List<MailDTO> mailListSearch_withPaging(Map<String, String> paraMap) {
		List<MailDTO> mailList = dao.mailListSearch_withPaging(paraMap);
		return mailList;
	}
	
	

}
