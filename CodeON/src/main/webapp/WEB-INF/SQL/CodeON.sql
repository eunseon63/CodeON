select *
from tbl_board

desc tbl_board
select *
from tbl_member

desc tbl_board
ALTER TABLE tbl_board MODIFY (FK_BOARD_TYPE_SEQ NULL);
ALTER TABLE tbl_board MODIFY (FK_MEMBER_SEQ NULL);
ALTER TABLE tbl_board MODIFY (BOARD_TITLE NULL);
ALTER TABLE tbl_board MODIFY (BOARD_CONTENT NULL);
commit

SELECT sequence_name
FROM user_sequences
WHERE sequence_name LIKE '%BOARD%';

select *
from tbl_board
CREATE SEQUENCE   board_category_seq  START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

SELECT sequence_name
FROM user_sequences
WHERE sequence_name LIKE '%BOARD%';

SELECT sequence_name
FROM user_sequences

select *
from tbl_board


INSERT INTO TBL_BOARD
  (Board_Seq, fk_Board_Type_Seq, fk_Member_Seq, board_Title, board_Content, board_Regdate, board_Readcount)
VALUES
  (TBL_BOARD_SEQ.nextval, 1, 1, '테스트 제목', '테스트 내용', SYSDATE, 0);

CREATE TABLE TBL_BOARD_CATEGORY (
    board_category_seq  NUMBER        NOT NULL,
    board_category_name VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_TBL_BOARD_CATEGORY PRIMARY KEY (board_category_seq)
);
-- 게시판 유형
CREATE TABLE TBL_BOARD_TYPE (
    board_type_seq  NUMBER        NOT NULL,
    board_type_name VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_TBL_BOARD_TYPE PRIMARY KEY (board_type_seq)
);

desc tbl_board
desc tbl_board_type
desc tbl_board_category

ALTER TABLE tbl_BOARD DROP CONSTRAINT FK_BOARD_TYPE;


drop table tbl_board


delete from tbl_board_type
where BOARD_type_SEQ= 1



select *
from tbl_board

select *
from tbl_member

SELECT BOARD_SEQ.NEXTVAL FROM DUAL;
desc tbl_board

desc tbl_board

desc tbl_board_type

select *
from tbl_board_type

desc tbl_board_type

select *
from tbl_board

INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (1, '부서게시판');  
INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (0, '사내게시판');  
INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (2, '경조사');  
INSERT INTO TBL_BOARD_TYPE (BOARD_TYPE_SEQ, BOARD_TYPE_NAME)
VALUES (1, '공지사항');  


INSERT INTO tbl_board_category (board_category_seq, board_category_name)
VALUES (0, '공지사항');  
INSERT INTO tbl_board_category (board_category_seq, board_category_name)
VALUES (1, '일반');  
INSERT INTO tbl_board_category (board_category_seq, board_category_name)
VALUES (2, '경조사');  


update TBL_BOARD_TYPE
set BOARD_TYPE_NAME='사내게시판'
where BOARD_TYPE_SEQ = 0


desc tbl_board


commit
SELECT * FROM TBL_BOARD_TYPE;

desc tbl_board_type

desc tbl_member

SELECT * FROM TBL_BOARD_TYPE WHERE BOARD_TYPE_SEQ = 1;


ALTER TABLE BOARD DROP CONSTRAINT FK_BOARD_TYPE; -- FK 이름은 실제 조회 결과에 맞게 변경
ALTER TABLE BOARD DROP CONSTRAINT FK_BOARD_MEMBER;



CREATE TABLE TBL_BOARD (
    board_seq         NUMBER        NOT NULL,
    fk_board_type_seq NUMBER        ,
    fk_member_seq     NUMBER        ,
    board_title       VARCHAR2(200) NOT NULL,
    board_content     CLOB          NOT NULL,
    board_regdate     DATE          DEFAULT SYSDATE,
    board_readcount   NUMBER        DEFAULT 0,
    CONSTRAINT PK_TBL_BOARD PRIMARY KEY (board_seq),
    CONSTRAINT FK_TBL_BOARD_TYPE_TO_TBL_BOARD FOREIGN KEY (fk_board_type_seq) REFERENCES TBL_BOARD_TYPE (board_type_seq),
    CONSTRAINT FK_TBL_MEMBER_TO_TBL_BOARD FOREIGN KEY (fk_member_seq) REFERENCES TBL_MEMBER (member_seq)
);


