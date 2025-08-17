package com.spring.app.service;

import static com.spring.app.entity.QDepartment.department;
import static com.spring.app.entity.QGrade.grade;
import static com.spring.app.entity.QMember.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;
import com.spring.app.model.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService_imple implements MemberService {

    private final MemberRepository memberRepository;
    private final JPAQueryFactory jPAQueryFactory;

    @PersistenceContext
    private EntityManager em;

    // 사용자 ID로 회원 정보 조회
    @Override
    public MemberDTO getMemberByUserId(String memberUserId) {

        MemberDTO mbrDto = null;

        try {
            Optional<Member> memberOpt = memberRepository.findByMemberUserid(memberUserId);

            Member mbr = memberOpt.get(); // 없으면 NoSuchElementException 발생
            mbrDto = mbr.toDTO();

        } catch (NoSuchElementException e) {
            // 로그인 실패 시 null 리턴
        }

        return mbrDto;
    }

    // 직원등록
    @Override
    @Transactional
    public Member registerMember(Member member) {

        String yearStr = String.valueOf(member.getMemberHiredate().getYear());
        String deptStr = String.format("%02d", member.getFkDepartmentSeq());

        // 시퀀스에서 다음 값 가져오기
        Integer seq = ((Number) em.createNativeQuery("SELECT MEMBER_SEQ_GENERATOR.NEXTVAL FROM DUAL")
            .getSingleResult()).intValue();

        // 3자리 포맷
        String seqStr = String.format("%03d", seq);

        String memberSeqStr = yearStr + deptStr + seqStr;
        int memberSeqInt = Integer.parseInt(memberSeqStr);

        member.setMemberSeq(memberSeqInt);
        member.setMemberUserid(member.getMemberUserid() + memberSeqStr);
        member.setMemberPwd(member.getMemberPwd() + memberSeqStr);
        member.setMemberEmail(member.getMemberEmail() + memberSeqStr + "@CodeON.com");

        return memberRepository.save(member);
    }
    
    // 직원수정
    @Override
    @Transactional
    public Member updateMember(Member member) {

        member.setMemberEmail(member.getMemberEmail() + "@CodeON.com");

        return memberRepository.save(member);
    }

	@Override
	public Page<Member> getPageMember(String searchType, String searchWord, String gender, int currentShowPageNo, int sizePerPage) throws Exception {
		
		Page<Member> page = Page.empty();
		
		try {

			Pageable pageable = PageRequest.of(currentShowPageNo - 1, sizePerPage, Sort.by(Sort.Direction.DESC, "memberHiredate"));
			
	         BooleanExpression condition = Expressions.TRUE; 
	         
	 		if ("fkDepartmentSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
			    // 부서명으로 조건 걸기 (조인)
			    condition = condition.and(department.departmentName.contains(searchWord));
			
			} else if ("memberName".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
			    
				condition = condition.and(member.memberName.contains(searchWord));
			
			} else if ("fkGradeSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
				
		        condition = condition.and(grade.gradeName.contains(searchWord));
		    }
			
			if ("0".equals(gender) || "1".equals(gender)) {
			    
				condition = condition.and(member.memberGender.eq(Integer.parseInt(gender)));
			
			}

			List<Member> members = jPAQueryFactory
			                        .selectFrom(member)
			                        .join(member.department, department)
			                        .join(member.grade, grade) // 조인
			                        .where(condition)
			   	                 	.offset(pageable.getOffset())
			   	                 	.limit(pageable.getPageSize())
			   	                 	.orderBy(member.memberHiredate.desc())
			                        .fetch();     
			
		    Long total = jPAQueryFactory
		                 .select(member.count())
		                 .from(member)
	                     .join(member.department, department)
	                     .join(member.grade, grade)
		                 .where(condition)
		                 .fetchOne();

	         page = new PageImpl<>(members, pageable, total != null ? total : 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return page;
	}

	// 회원 삭제
	@Override
	public int delete(int memberSeq) {
	    int n = 0;
	    try {
	        memberRepository.deleteById(memberSeq);
	        n = 1;
	    } catch (EmptyResultDataAccessException e) {
	        e.printStackTrace();
	    }
	    return n;
	}

	// 직원 찾기
	@Override
	public MemberDTO getMemberOne(String memberSeq) {
		
		int seq = Integer.parseInt(memberSeq);
		
		BooleanExpression condition = Expressions.TRUE;
		
		condition = member.memberSeq.eq(seq);
		
	    Member mbr = jPAQueryFactory
	                .selectFrom(member)
	                .where(condition)
	                .fetchOne();
        
	    return mbr.toDTO();
	}
	
	// 검색 회원 조회
	@Override
	public List<MemberDTO> searchMember(Map<String, String> paraMap) {
		List<MemberDTO> memberDtoList = new ArrayList<>();
		
		String searchType = paraMap.get("searchType");
		String searchWord = paraMap.get("searchWord");
		String gender = paraMap.get("gender");
		
		// >>> BooleanExpression은 QueryDSL 에서 제공해주는 클래스 이다.
		// BooleanExpression 클래스는 QueryDSL 전용의 SQL의 WHERE 조건 표현 객체로서 QueryDSL의 .where(), .and(), .or() 에만 사용된다. <<<
		
		BooleanExpression condition = Expressions.TRUE;
		// Expressions.TRUE 라고 준것은 기본 조건 (항상 참)으로 시작해서 조건을 점진적으로 추가한다. 마치 WHERE 1=1 과 같은 뜻이다. 
		
		if ("fkDepartmentSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
		    // 부서명으로 조건 걸기 (조인)
		    condition = condition.and(department.departmentName.contains(searchWord));
		
		} else if ("memberName".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
		    
			condition = condition.and(member.memberName.contains(searchWord));
		
		} else if ("fkGradeSeq".equals(searchType) && (searchWord != null && !searchWord.trim().isEmpty())) {
			
	        condition = condition.and(grade.gradeName.contains(searchWord));
	    }
		
		if ("0".equals(gender) || "1".equals(gender)) {
		    
			condition = condition.and(member.memberGender.eq(Integer.parseInt(gender)));
		
		}

		List<Member> members = jPAQueryFactory
		                        .selectFrom(member)
		                        .join(member.department, department)
		                        .join(member.grade, grade) // 조인
		                        .where(condition)
		                        .fetch();
		/*
		for (Member mbr : members) {
			memberDtoList.add(mbr.toDTO());
		}
		*/
		
		memberDtoList = members.stream()
							   .map(Member::toDTO)
							   .collect(Collectors.toList());

		return memberDtoList;
	}


}

