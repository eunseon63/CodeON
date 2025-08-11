package com.spring.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    @Column(name = "member_seq", length = 9)  // YYYY(4) + dept(2) + seq(3) 총 9자리 문자열
    private String memberSeq;

    // ====== 연관관계 매핑 ======
	@Column(name = "fk_grade_seq", nullable = false)
	private int fkGradeSeq;

	@Column(name = "fk_department_seq", nullable = false)
	private int fkDepartmentSeq;

    // ====== 일반 컬럼 ======
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
    private LocalDateTime memberHiredate;

    @Column(name = "member_jubun", nullable = false, length = 30)
    private String memberJubun;

    @Column(name = "member_mobile", nullable = false, length = 30)
    private String memberMobile;

    @Column(name = "stamp_image", length = 50)
    private String stampImage;
}

