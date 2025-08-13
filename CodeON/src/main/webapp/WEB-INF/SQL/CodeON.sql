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