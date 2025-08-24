package com.spring.app.domain;

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
public class DraftLineDTO {
	
	private int draftLineSeq;
	private int fkDraftSeq;
	private int fkMemberSeq;
	private int lineOrder;
	private String approvalStatus;
	private String rejectReason;
	private String approvalDate;
    
}
