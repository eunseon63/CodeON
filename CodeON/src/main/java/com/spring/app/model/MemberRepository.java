package com.spring.app.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
	
}
