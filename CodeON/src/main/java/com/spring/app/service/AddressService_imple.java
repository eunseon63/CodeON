package com.spring.app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.spring.app.domain.AddressDTO;
import com.spring.app.entity.Department;
import com.spring.app.model.DepartmentRepository;
import com.spring.app.model.MemberRepository;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService_imple implements AddressService {
	
	private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<Department> departments() {
        return departmentRepository.findAll(Sort.by(Sort.Order.asc("departmentSeq")));
    }

    @Override
    public Page<AddressDTO> search(Integer dept, String kw, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
        Long deptParam = (dept == null ? null : dept.longValue());
        return memberRepository.searchAddress(deptParam, kw, pageable);
    }

}
