package com.spring.app.entity;

import java.time.LocalDate;

import com.spring.app.domain.MemberDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TBL_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @Column(name = "member_seq", length = 9)
    private int memberSeq;

    @Column(name = "fk_grade_seq", nullable = false)
    private int fkGradeSeq;

    @Column(name = "fk_department_seq", nullable = false)
    private int fkDepartmentSeq;
    
    @ManyToOne
    @JoinColumn(name = "fk_grade_seq", insertable = false, updatable = false)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "fk_department_seq", insertable = false, updatable = false)
    private Department department;

    @Column(name = "member_name", nullable = false, length = 30)
    private String memberName;

    @Column(name = "member_userid", nullable = false, length = 30)
    private String memberUserid;

    @Column(name = "member_pwd", nullable = false, length = 30)
    private String memberPwd;

    @Column(name = "member_email", nullable = false, length = 50, unique = true)
    private String memberEmail;

    @Column(name = "member_salary")
    private Long memberSalary;

    @Column(name = "member_hiredate", nullable = false)
    private LocalDate memberHiredate;

    @Column(name = "member_birthday", nullable = false, length = 30)
    private String memberBirthday;

    @Column(name = "member_mobile", nullable = false, length = 30)
    private String memberMobile;

    @Column(name = "member_gender", nullable = false, length = 10)
    private int memberGender;

    @Column(name = "stamp_image", length = 50)
    private String stampImage;

    public MemberDTO toDTO() {
        return MemberDTO.builder()
                .memberSeq(this.memberSeq)
                .fkGradeSeq(this.fkGradeSeq)
                .fkDepartmentSeq(this.fkDepartmentSeq)
                .memberName(this.memberName)
                .memberUserid(this.memberUserid)
                .memberPwd(this.memberPwd)
                .memberEmail(this.memberEmail)
                .memberSalary(memberSalary != null ? memberSalary : 0L)
                .memberHiredate(this.memberHiredate)
                .memberBirthday(this.memberBirthday)
                .memberMobile(this.memberMobile)
                .memberGender(this.memberGender)
                .stampImage(this.stampImage)
                .build();
    }
}



