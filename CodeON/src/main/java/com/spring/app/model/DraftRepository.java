package com.spring.app.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.app.entity.Draft;

public interface DraftRepository extends JpaRepository<Draft, Long> {

	 // 문서 번호 미리보기
	@Query(value = """
	        SELECT last_number
	        FROM user_sequences
	        WHERE sequence_name = 'DRAFT_SEQ'
	        """, nativeQuery = true)
	Long peekNextDraftNo();
	
	List<Draft> findByMember_MemberSeqOrderByDraftRegdateDesc(Long memberSeq);

	List<Draft> findByMember_MemberSeqOrderByDraftSeqDesc(Long me);
	
	@Query("""
		    select d
		      from Draft d
		      left join fetch d.draftType
		      left join fetch d.member
		     where d.draftSeq = :id
		""")
	Optional<Draft> findDetail(@Param("id") Long id);

    @Query("""
        select d
          from Draft d
          join fetch d.member m
          left join fetch d.draftType dt
         where d.draftSeq = :seq
    """)
    Optional<Draft> findByIdWithMemberAndType(@Param("seq") Long seq);
    
    @Query("""
	  select d
	    from Draft d
	    left join fetch d.draftType 
	    left join fetch d.member       
	   where d.member.memberSeq = :me
	   order by d.draftSeq desc
	""")
	List<Draft> findByMemberWithType(@Param("me") Long me);

    @Query("""
    	    select d
    	      from Draft d
    	      join fetch d.member m
    	      left join fetch m.department
    	      left join fetch m.grade
    	      left join fetch d.draftType
    	     where d.draftSeq = :seq
    	""")
    	Optional<Draft> findByIdWithMemberTypeAndOrg(@Param("seq") Long seq);
    
}
