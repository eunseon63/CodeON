package com.spring.app.model;

import java.util.List;
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

	@Query("select m.stampImage from Member m where m.memberUserid = :userid")
	String findStampImageByUserid(@Param("userid") String userid);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update Member m set m.stampImage = null where m.memberUserid = :userid")
	int clearStampImageByUserid(@Param("userid") String userid);

	@Query("select m from Member m left join fetch m.department")
	List<Member> findAllWithDept();
	
	// 부서별 직원
	List<Member> findAllByOrderByFkDepartmentSeqAsc();

	// 결재라인에 추가할 수 있는 직원(사원 제외 전부)
	@Query("select m from Member m left join fetch m.department d where m.fkGradeSeq <> 1 order by m.fkDepartmentSeq asc, m.memberName asc")
	List<Member> getSignlineMember();
	
}
