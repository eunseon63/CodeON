package com.spring.app.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.entity.Member;

import jakarta.transaction.Transactional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	Optional<Member> findByMemberUserid(String memberUserid);

	@Modifying
	@Transactional
	@Query("UPDATE Member m SET m.stampImage = :newFilename WHERE m.memberUserid = :memberUserid")
	void stampImageSave(@Param("memberUserid") String memberUserid, @Param("newFilename") String newFilename);

}
