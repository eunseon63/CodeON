package com.spring.app.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.domain.AddressDTO;
import com.spring.app.entity.Member;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	Optional<Member> findByMemberUserid(String memberUserid);

	@Modifying
	@Transactional
	@Query("UPDATE Member m SET m.stampImage = :newFilename WHERE m.memberUserid = :memberUserid")
	void stampImageSave(@Param("memberUserid") String memberUserid, @Param("newFilename") String newFilename);

	@Query("""
			  select new com.spring.app.domain.AddressDTO(
			    d.departmentSeq, d.departmentName,
			    m.memberSeq, m.memberName, m.memberEmail, m.memberMobile, m.memberUserid
			  )
			  from Member m
			  join m.department d
			  where (:dept is null or d.departmentSeq = :dept)
			    and (
			      :kw is null or :kw = '' or
			      lower(m.memberName)   like lower(concat('%', :kw, '%')) or
			      lower(m.memberEmail)  like lower(concat('%', :kw, '%')) or
			      lower(m.memberMobile) like lower(concat('%', :kw, '%')) or
			      lower(m.memberUserid) like lower(concat('%', :kw, '%'))
			    )
			  order by d.departmentSeq asc, m.memberName asc
			""")
			Page<AddressDTO> searchAddress(@Param("dept") Long dept,
			                               @Param("kw")   String kw,
			                               Pageable pageable);
}
