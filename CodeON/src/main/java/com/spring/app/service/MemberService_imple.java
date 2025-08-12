package com.spring.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    
	@Override
	public List<MemberDTO> getAllMember() {
		
		List<MemberDTO> memberDtoList = new ArrayList<>();
		
		List<Member> members = memberRepository.findAll();
		
		memberDtoList = members.stream().map(Member::toDTO).collect(Collectors.toList());
		
		return memberDtoList;
	}


    
}
