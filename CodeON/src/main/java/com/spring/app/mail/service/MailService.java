package com.spring.app.mail.service;

import java.util.List;
import java.util.Map;

import com.spring.app.mail.domain.MailDTO;

public interface MailService {

	// 파일첨부가 없는 글쓰기
	int write(MailDTO mailDto);

	// 파일첨부가 있는 글쓰기 
	int write_withFile(MailDTO mailDto);

	// 총 메일 (totalCount) 구하기
	int getTotalCount(Map<String, String> paraMap);

	// 메일목록 가져오기
	List<MailDTO> mailListSearch_withPaging(Map<String, String> paraMap);

}
