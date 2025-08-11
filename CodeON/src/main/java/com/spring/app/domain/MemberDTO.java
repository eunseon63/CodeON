package com.spring.app.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {
	
    private String memberSeq;
    private int fkGradeSeq;
    private int fkDepartmentSeq;
    private String memberName;
    private String memberUserid;
    private String memberPwd;
    private String memberEmail;
    private int memberSalary;
    private LocalDateTime memberHiredate;
    private String memberJubun;
    private String memberMobile;
    private String stampImage;
}
