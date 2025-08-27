package com.spring.app.mail.domain;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDTO {

    private Long emailSeq;
    private Long fkMemberSeq;
    private String sendMemberEmail;
    private String receiveMemberEmail;
    private Long emailSendOrgno;
    private Long emailReceiveOrgno;
    private String emailTitle;
    private String emailContent;
    private LocalDateTime emailRegdate;
    private Integer emailSendStatus;
    private Integer emailReceiveStatus;
    private String emailFilename;
    private String emailOrgFilename;
    private String emailFilesize;
    private Integer emailSendImportant;
    private Integer emailReceiveImportant;
    private Integer emailReadStatus;
}
