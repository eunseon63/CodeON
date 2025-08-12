select * from tab;
select * from tbl_member;

----- ==== *** 휴지통 조회하기 *** ==== -----
select * 
from user_recyclebin;

purge recyclebin;  -- 휴지통에 있던 모든 테이블들을 영구히 삭제하는 것이다.
-- Recyclebin이(가) 비워졌습니다.

drop sequence seq_department;

CREATE SEQUENCE seq_department
START WITH 10
INCREMENT BY 10
NOCACHE;

insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '인사팀');
insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '개발팀');
insert into TBL_DEPARTMENT values(SEQ_DEPARTMENT.nextval, '기획팀');
commit;

insert into TBL_GRADE values(SEQ_GRADE.nextval, '사원');
insert into TBL_GRADE values(SEQ_GRADE.nextval, '대리');
insert into TBL_GRADE values(SEQ_GRADE.nextval, '과장');
commit;

CREATE SEQUENCE MEMBER_SEQ_GENERATOR
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;
/

commit;