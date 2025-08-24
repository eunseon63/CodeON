package com.spring.app.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.spring.app.entity.Draft;

public interface DraftRepository extends JpaRepository<Draft, Long> {

	 // 문서 번호 미리보기
	@Query(value = """
	        SELECT last_number
	        FROM user_sequences
	        WHERE sequence_name = 'DRAFT_SEQ'
	        """, nativeQuery = true)
	Long peekNextDraftNo();
}
