// com.spring.app.model.DraftLineRepository.java
package com.spring.app.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.entity.DraftLine;

public interface DraftLineRepository extends JpaRepository<DraftLine, Long> {
	
    /** 결재하기: 내가 다음 결재자이면서 대기(0) */
    @Query("""
    select distinct dl
      from DraftLine dl
      join fetch dl.draft d
      join fetch d.member drafter
      left join fetch d.draftType dt
     where dl.approver.memberSeq = :memberSeq
       and dl.signStatus = 0
       and dl.lineOrder = (
         select min(dl2.lineOrder)
           from DraftLine dl2
          where dl2.draft = d
            and dl2.signStatus <> 1
       )
     order by d.isEmergency desc, d.draftRegdate desc
    """)
    List<DraftLine> findInbox(@Param("memberSeq") Long memberSeq);

    /** 결재함: 내가 승인/반려 했던 내역 */
    @Query("""
    select distinct dl
      from DraftLine dl
      join fetch dl.draft d
      join fetch d.member drafter
      left join fetch d.draftType dt
     where dl.approver.memberSeq = :memberSeq
       and dl.signStatus in (1,9)
     order by dl.signDate desc nulls last, d.draftRegdate desc
    """)
    List<DraftLine> findHistory(@Param("memberSeq") Long memberSeq);
    
    List<DraftLine> findByDraft_DraftSeqOrderByLineOrderAscDraftLineSeqAsc(Long draftSeq);

	
}
