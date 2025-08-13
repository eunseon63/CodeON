package com.spring.app.board.domain;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BoardDTO {
    private String boardSeq;        
    private String fk_Board_Type_Seq;
    private String fk_Member_Seq;    
    private String board_Title;       
    private String board_Content;      
    private String board_Regdate;      
    private String board_Readcount;  

}