package com.spring.app.entity;

import java.time.LocalDateTime;

import com.spring.app.domain.SignDTO;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_SIGN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sign {

    @Id
    @Column(name = "draft_seq")
    private Long draftSeq;

    @Column(name = "fk_draft_type_seq", nullable = false)
    private Long fkDraftTypeSeq;

    @Column(name = "fk_member_seq", nullable = false)
    private Long fkMemberSeq;

    @Column(name = "draft_title", nullable = false, length = 200)
    private String draftTitle;

    @Lob
    @Column(name = "draft_content", nullable = false)
    private String draftContent;

    @Column(name = "draft_status", nullable = false)
    private Integer draftStatus = 0; // 기본값 0

    @Column(name = "is_emergency", nullable = false)
    private Integer isEmergency = 0; // 기본값 0

    @Column(name = "draft_regdate", nullable = false)
    private LocalDateTime draftRegdate = LocalDateTime.now();
}
