package com.spring.app.domain;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

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
	
    private int memberSeq;
    private int fkGradeSeq;
    private int fkDepartmentSeq;
    private String memberName;
    private String memberUserid;
    private String memberPwd;
    private String memberEmail;
    private long memberSalary;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate memberHiredate;
    private String memberMobile;
    private String memberBirthday;
    private int memberGender;
    private String stampImage;
}