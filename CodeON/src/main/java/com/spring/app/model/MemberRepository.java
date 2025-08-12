package com.spring.app.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.app.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberUserId(String memberUserId);
}
