package com.spring.app.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignDTO {

    private Long draftSeq;
    private Long fkDraftTypeSeq;
    private Long fkMemberSeq;
    private String draftTitle;
    private String draftContent;
    private Integer draftStatus;
    private Integer isEmergency;
    private LocalDateTime draftRegdate;
}
