package com.spring.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_SIGNLINE_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignlineMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "signlineMemberSeqGen")
    @SequenceGenerator(name = "signlineMemberSeqGen", sequenceName = "SIGNLINE_MEMBER_SEQ", allocationSize = 1)
    @Column(name = "signline_member_seq")
    private Long signlineMemberSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_signline_seq", nullable = false)
    private Signline signline;

    @Column(name = "fk_member_seq", nullable = false)
    private Integer memberSeq; // 결재자(회원)

    @Column(name = "line_order", nullable = false)
    private Integer lineOrder; // 결재 순서
    
    public SignlineMember toDTO() {
        return SignlineMember.builder()
                .signline(this.signline)
                .memberSeq(this.memberSeq)
                .lineOrder(this.lineOrder)
                .build();
    }
}
