package com.spring.app.board.domain;

import lombok.Data;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardDTO {
    private Integer board_seq;
    private Integer fk_board_type_seq;
    private Integer fk_board_category_seq;
    private Integer fk_member_seq;
    private String board_title;
    private String board_content;
    private Date board_regdate;
    private Integer board_readcount;
    private String board_password;
    private String board_file_ori_name;
    private String board_file_save_name;
    private Long board_file_size;
    private MultipartFile attach;
    // join 용
    private String member_name;
    private String board_category_name;
    private Integer comment_count;
}
