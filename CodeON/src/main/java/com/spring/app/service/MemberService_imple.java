package com.spring.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.entity.Member;
import com.spring.app.model.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService_imple implements MemberService {

	private final MemberRepository memberRepository;
	
    @PersistenceContext
    private EntityManager em;
    
	@Override
	public Member registerMember(Member member) {
		return memberRepository.save(member);
	}

    @Transactional
    public void saveMember(Member member) {
        // 여기에 ID 생성 로직 작성
        // 1. 입사년도, 2. 부서번호 2자리, 3. seq 3자리 조회 및 생성

        String year = String.valueOf(member.getMemberHiredate().getYear());
        String deptCode = String.format("%02d", member.getFkDepartmentSeq());

        String sql = """
            SELECT NVL(MAX(TO_NUMBER(SUBSTR(m.member_seq, 7, 3))), 0) + 1
            FROM TBL_MEMBER m
            WHERE SUBSTR(m.member_seq, 1, 4) = :year
              AND SUBSTR(m.member_seq, 5, 2) = :dept
        """;

        Integer seq = ((Number) em.createNativeQuery(sql)
            .setParameter("year", year)
            .setParameter("dept", deptCode)
            .getSingleResult()).intValue();

        String seqStr = String.format("%03d", seq);

        String memberSeq = year + deptCode + seqStr;
        member.setMemberSeq(memberSeq);

        em.persist(member);
    }
}


