package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Sign;

public interface SignRepository extends JpaRepository<Sign, Integer> {
    List<Sign> findByFkMemberSeqOrderByDraftRegdateDesc(Long fkMemberSeq);
}
