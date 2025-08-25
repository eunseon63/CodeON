package com.spring.app.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO {

	List<Map<String, String>> memberCntByDeptname();

	List<Map<String, String>> memberCntByGender();
	

}
