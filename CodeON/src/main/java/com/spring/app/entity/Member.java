package com.spring.app.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TBL_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Member {

    @Id
    @Column(name = "member_seq")
    private Long memberSeq;

    @Column(name = "member_name", nullable = false, length = 30)
    private String memberName;

    @Column(name = "member_userid", nullable = false, length = 30)
    private String memberUserid;

    @Column(name = "member_pwd", nullable = false, length = 30)
    private String memberPwd;

    @Column(name = "member_email", nullable = false, length = 50, unique = true)
    private String memberEmail;

    @Column(name = "member_salary")
    private Integer memberSalary;

    @Column(name = "member_hiredate", nullable = false)
    private LocalDate memberHiredate;

    @Column(name = "member_jubun", nullable = false, length = 30)
    private String memberJubun;

    @Column(name = "member_mobile", nullable = false, length = 30)
    private String memberMobile;

    @Column(name = "stamp_image", length = 50)
    private String stampImage;

    // === 연관 관계 ===

	/*
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "fk_grade_seq", referencedColumnName = "grade_seq",
	 * insertable = false, updatable = false) private Grade grade;
	 * 
	 * @ManyToOne
	 * 
	 * @JoinColumn(name = "fk_department_seq", referencedColumnName =
	 * "department_seq", insertable = false, updatable = false) private Department
	 * department;
	 */
}
