package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBL_DRAFT_LINE")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DraftLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DRAFT_LINE_GEN")
    @SequenceGenerator(name = "SEQ_DRAFT_LINE_GEN", sequenceName = "SEQ_DRAFT_LINE", allocationSize = 1)
    @Column(name = "draft_line_seq", nullable = false)
    private Long draftLineSeq;

    // FK: TBL_DRAFT(draft_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_draft_seq", nullable = false)
    private Draft draft;

    // FK: TBL_MEMBER(member_seq)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_member_seq", nullable = false)
    private Member approver;

    @Column(name = "line_order", nullable = false)
    private Integer lineOrder;

    @Column(name = "approval_status", length = 50)
    private String approvalStatus;  // DEFAULT '대기' (DB 기본값 사용)

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}