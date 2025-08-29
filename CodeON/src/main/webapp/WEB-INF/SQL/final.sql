select * from tab;
select * from tbl_member;
select * from TBL_GRADE;
SELECT sequence_name
FROM user_sequences;
desc tbl_member;
----- ==== *** 휴지통 조회하기 *** ==== -----
select * 
from user_recyclebin;

purge recyclebin;  -- 휴지통에 있던 모든 테이블들을 영구히 삭제하는 것이다.
-- Recyclebin이(가) 비워졌습니다.
commit;
drop sequence seq_department;

CREATE SEQUENCE seq_department
START WITH 10
INCREMENT BY 10
NOCACHE;
select * from TBL_DEPARTMENT;

delete from TBL_DEPARTMENT
where department_seq = '60';

insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '인사팀');
insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '개발팀');
insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '기획팀');
insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '영업팀');
insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '고객지원팀');
commit;

insert into TBL_GRADE values(SEQ_GRADE.nextval, '사원');
insert into TBL_GRADE values(SEQ_GRADE.nextval, '대리');
insert into TBL_GRADE values(SEQ_GRADE.nextval, '과장');
insert into TBL_GRADE values(SEQ_GRADE.nextval, '부장');
insert into TBL_GRADE values(SEQ_GRADE.nextval, '사장');
commit;

drop SEQUENCE MEMBER_SEQ_GENERATOR;
CREATE SEQUENCE MEMBER_SEQ_GENERATOR
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;
/

commit;

WITH
A AS
(SELECT department_name
      , COUNT(*) AS cnt
 FROM tbl_member E LEFT JOIN tbl_department D
 ON E.fk_department_seq = D.department_seq
 GROUP BY D.department_name)
,
B AS
(SELECT COUNT(*) AS totalcnt
 FROM tbl_member)
SELECT NVL(department_name, '부서없음') AS department_name, 
       cnt, 
       ROUND((cnt/totalcnt)*100, 2) AS percentage
FROM A CROSS JOIN B 
ORDER BY cnt DESC, A.department_name ASC;

with
A as
(
select member_gender as gender, count(*) as cnt
from tbl_member
group by member_gender
)
,
B as
(
select count(*) as totalcnt
from tbl_member
)
select gender,
    cnt,
    ROUND((cnt/totalcnt)*100, 2) AS percentage
FROM A CROSS JOIN B
order by cnt desc;

desc TBL_CALENDAR;
desc TBL_CALENDAR_BIG_CATEGORY;
desc TBL_CALENDAR_SMALL_CATEGORY;
select * from tab;
select * from TBL_CALENDAR;

select *
from TBL_DRAFT;

drop table TBL_EMAIL;

CREATE TABLE TBL_EMAIL (
    email_seq NUMBER PRIMARY KEY,
    send_member_email VARCHAR2(50) NOT NULL,
    receive_member_email VARCHAR2(50),
    email_send_orgno NUMBER,
    email_receive_orgno NUMBER,
    email_title VARCHAR2(50),
    email_content CLOB,
    email_regdate DATE DEFAULT SYSDATE,
    email_send_status NUMBER(1) DEFAULT 0,
    email_receive_status NUMBER(1) DEFAULT 0,
    email_filename VARCHAR2(255),
    email_orgfilename VARCHAR2(255),
    email_filesize VARCHAR2(255),
    email_send_important NUMBER(1) DEFAULT 0,
    email_receive_important NUMBER(1) DEFAULT 0,
    email_readstatus NUMBER(1) DEFAULT 0,
    CONSTRAINT fk_email_send_member_email FOREIGN KEY (send_member_email)
        REFERENCES TBL_MEMBER(member_email)
);

CREATE SEQUENCE emailSeq
START WITH 1
INCREMENT BY 1
NOCACHE;

select * from tbl_member;
desc tbl_member;
select * from tab;
select * from tbl_vacation;
desc tbl_vacation;
select * from tbl_business;
desc tbl_business;
select * from tbl_payment;
select * from tbl_business_conform;

select * from tab;
select * from tbl_member;
select * from tbl_email;
desc tbl_email;

select count(*)
from tbl_email
