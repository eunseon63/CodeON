package com.spring.app.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.spring.app.domain.MemberDTO;
import com.spring.app.entity.Member;
import com.spring.app.model.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService_imple implements MemberService {

    private final MemberRepository memberRepository;

    // 사용자 ID로 회원 정보 조회
    @Override
    public MemberDTO getMemberByUserId(String memberUserId) {

        MemberDTO mbrDto = null;

        try {
            Optional<Member> memberOpt = memberRepository.findByMemberUserId(memberUserId);

            Member mbr = memberOpt.get(); // 없으면 NoSuchElementException 발생
            mbrDto = mbr.toDTO();

        } catch (NoSuchElementException e) {
            // 로그인 실패 시 null 리턴
        }

        return mbrDto;
    }
}
