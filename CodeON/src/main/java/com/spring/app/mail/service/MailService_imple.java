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

	// 별 업데이트
	@Override
	public int updateImportant(Map<String, String> paraMap) {
		return dao.updateImportant(paraMap);
	}

	// 읽음 업데이트
	@Override
	public int updateReadStatus(Map<String, String> paraMap) {
		return dao.updateReadStatus(paraMap);
	}

	// 메일 select
	@Override
	public MailDTO selectOne(String emailSeq) {
		return dao.selectOne(emailSeq);
	}

	// 읽은 메일 개수
	@Override
	public String getCount() {
		return dao.getCount();
	}

	// 총 메일 개수 구하기
	@Override
	public String getTotalCount() {
		return dao.totalCount();
	}

	// 메일 여러개 삭제하기
	@Override
	public int deleteMails(List<Long> emailSeqList) {
		return dao.deleteMails(emailSeqList);
	}

	// 메일 하나 삭제하기
	@Override
	public int deleteMail(String emailSeq) {
		return dao.deleteMail(emailSeq);
	}
	
	
	
	

}
