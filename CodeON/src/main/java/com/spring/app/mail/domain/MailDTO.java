package com.spring.app.mail.domain;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDTO {

    private String emailSeq;
    private String sendMemberEmail;
    private String receiveMemberEmail;
    private String emailSendOrgno;
    private String emailReceiveOrgno;
    private String emailTitle;
    private String emailContent;
    private String emailRegdate;
    private String emailSendStatus;
    private String emailReceiveStatus;
    
    private MultipartFile attach;
    
    private String emailFilename;
    private String emailOrgFilename;
    private String emailFilesize;
    
    private String emailSendImportant;
    private String emailReceiveImportant;
    private String emailReadStatus;
}
