package com.spring.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.app.domain.SignlineDTO;
import com.spring.app.entity.Signline;
import com.spring.app.model.SignlineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignlineService_imple implements SignlineService {
	
	private final SignlineRepository signlineRepository;

	public Object findDetail(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SignlineDTO> findAllByOwner(int memberSeq) {
        Long owner = Long.valueOf(memberSeq);
        List<Signline> rows = signlineRepository
                .findByFkMemberSeqOrderBySignlineSeqDesc(owner);

        List<SignlineDTO> list = new java.util.ArrayList<>(rows.size());
        for (Signline s : rows) {
            list.add(s.toDTO());   // 엔티티의 toDTO 사용 (members 안 끌어오면 Lazy 문제 X)
        }
        return list;
	}

}
