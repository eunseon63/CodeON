select * from tab;

----- ==== *** 휴지통 조회하기 *** ==== -----
select * 
from user_recyclebin;

purge recyclebin;  -- 휴지통에 있던 모든 테이블들을 영구히 삭제하는 것이다.
-- Recyclebin이(가) 비워졌습니다.

CREATE SEQUENCE seq_department
START WITH 10
INCREMENT BY 10
NOCACHE;

select *
from user_sequences
where sequence_name = 'seq_department';

