package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Signline;

public interface SignlineRepository extends JpaRepository<Signline, Long> {
	
	// 로그인 사용자의 결재라인 목록 반환
	List<Signline> findByFkMemberSeqOrderBySignlineSeqDesc(Long fkMemberSeq);

}
