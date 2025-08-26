package com.spring.app.board.domain;

import lombok.Data;
import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BoardDTO {
    private Integer boardSeq;
    private Integer fkBoardTypeSeq;
    private Integer fkBoardCategorySeq;
    private Integer fkMemberSeq;
    private String boardTitle;
    private String boardContent;
    private Date boardRegdate;
    private Integer boardReadcount;
    private String boardPassword;
    private String boardFileOriName;
    private String boardFileSaveName;
    private Long boardFileSize;
    private MultipartFile attach;

    // join 용
    private String memberName;
    private String boardCategoryName;
    private Integer commentCount;
}