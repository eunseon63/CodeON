package com.spring.app.domain;

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

    private Long memberSeq;         // 회원 번호 (PK)
    private Long fkDepartmentSeq;   // 부서 번호
    private Long fkGradeSeq;        // 직급 번호
    private Integer memberGender;   // 성별
    private String memberHireDate;  // 입사일 (문자열 또는 LocalDate 가능)
    private Long memberSalary;      // 급여
    private String memberBirthday;  // 생년월일
    private String memberMobile;    // 전화번호
    private String memberName;      // 이름
    private String memberPwd;       // 비밀번호
    private String memberUserId;    // 아이디
    private String memberEmail;     // 이메일
    private String stampImage;      // 도장 이미지 파일명
}
