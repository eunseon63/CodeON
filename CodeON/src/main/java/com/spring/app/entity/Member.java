package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.spring.app.domain.MemberDTO;

@Entity
@Table(name = "tbl_member") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member {

    @Id
    @Column(name = "MEMBER_SEQ", nullable = false)
    @SequenceGenerator(
            name = "SEQ_MEMBER_GENERATOR",
            sequenceName = "seq_tbl_member", 
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GENERATOR")
    private Long memberSeq;

    @Column(name = "FK_DEPARTMENT_SEQ", nullable = false)
    private Long fkDepartmentSeq;

    @Column(name = "FK_GRADE_SEQ", nullable = false)
    private Long fkGradeSeq;

    @Column(name = "MEMBER_GENDER", nullable = false)
    private Integer memberGender;

    @Column(name = "MEMBER_HIREDATE", nullable = false)
    private LocalDate memberHireDate;

    @Column(name = "MEMBER_SALARY")
    private Long memberSalary;

    @Column(name = "MEMBER_BIRTHDAY", nullable = false, length = 30)
    private String memberBirthday;

    @Column(name = "MEMBER_MOBILE", nullable = false, length = 30)
    private String memberMobile;

    @Column(name = "MEMBER_NAME", nullable = false, length = 30)
    private String memberName;

    @Column(name = "MEMBER_PWD", nullable = false, length = 30)
    private String memberPwd;

    @Column(name = "MEMBER_USERID", nullable = false, length = 30)
    private String memberUserId;

    @Column(name = "MEMBER_EMAIL", nullable = false, length = 50)
    private String memberEmail;

    @Column(name = "STAMP_IMAGE", length = 50)
    private String stampImage;
    
    public MemberDTO toDTO() {
        return MemberDTO.builder()
                .memberSeq(this.memberSeq)
                .fkDepartmentSeq(this.fkDepartmentSeq)
                .fkGradeSeq(this.fkGradeSeq)
                .memberGender(this.memberGender)
                .memberHireDate(this.memberHireDate != null ? this.memberHireDate.toString() : null)
                .memberSalary(this.memberSalary)
                .memberBirthday(this.memberBirthday)
                .memberMobile(this.memberMobile)
                .memberName(this.memberName)
                .memberPwd(this.memberPwd)
                .memberUserId(this.memberUserId)
                .memberEmail(this.memberEmail)
                .stampImage(this.stampImage)
                .build();
    }

}
