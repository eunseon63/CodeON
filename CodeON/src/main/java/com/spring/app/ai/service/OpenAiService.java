package com.spring.app.ai.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import com.spring.app.ai.domain.DocumentDTO;
import com.spring.app.ai.model.DocumentRepository;
import com.spring.app.ai.model.VectorStoreRepository;
import com.spring.app.domain.MemberDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel; 
    private final DocumentRepository documentRepository;
    private final VectorStoreRepository vectorStoreRepository;

    /**
     * HR 데이터 분석 요약
     */
    public String memberChat(List<MemberDTO> members) {
        StringBuilder sb = new StringBuilder();
        for (MemberDTO m : members) {
            sb.append(String.format("이름:%s, 부서:%d, 직급:%d, 입사일:%s, 성별:%d\n",
                    m.getMemberName(), m.getFkDepartmentSeq(), m.getFkGradeSeq(),
                    m.getMemberHiredate(), m.getMemberGender()));
        }

        String prompt = """
                당신은 HR 데이터 분석 도우미입니다.
                아래 사원 목록 데이터를 요약해 주세요:
                %s

                출력 형식:
                1. 전체 인원 수
                2. 부서별(10:인사팀, 20:개발팀, 30:기획팀, 40:영업팀, 50:고객지원팀) 인원 수
                   직급별(1:사원, 2:대리, 3:과장, 4:부장, 5:사장) 인원수 와 평균 근속 연수
                3. 성별(0:남자, 1:여자) 비율
                4. 관리자가 참고할 인사이트 (2~3줄)
                """.formatted(sb);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * DB 전체 테이블 → DocumentDTO 변환 → 벡터 인덱싱
     */
    @Transactional
    public void indexAllDbDocuments() {
        List<DocumentDTO> allDocuments = documentRepository.fetchAllTables();

        for (DocumentDTO doc : allDocuments) {
            String combined;

            switch (doc.getTableName()) {
                case "TBL_MEMBER":
                    combined = "사원 정보: " + buildNaturalSentence(doc); break;
                case "TBL_BOARD":
                    combined = "게시글: " + buildNaturalSentence(doc); break;
                case "TBL_COMMENT":
                    combined = "댓글: " + buildNaturalSentence(doc); break;
                case "TBL_EMAIL":
                    combined = "메일: " + buildNaturalSentence(doc); break;
                case "TBL_DRAFT":
                    combined = "문서: " + buildNaturalSentence(doc); break;
                case "TBL_BUSINESS":
                    combined = "업무보고: " + buildNaturalSentence(doc); break;
                case "TBL_BUSINESS_CONFORM":
                    combined = "결재 의견: " + buildNaturalSentence(doc); break;
                case "TBL_CALENDAR":
                    combined = "일정: " + buildNaturalSentence(doc); break;
                case "TBL_ANNUAL_LEAVE":
                    combined = "연차: " + buildNaturalSentence(doc); break;
                case "TBL_ATTENDANCE":
                    combined = "출퇴근 기록: " + buildNaturalSentence(doc); break;
                case "TBL_PAYMENT":
                    combined = "급여 지급: " + buildNaturalSentence(doc); break;
                case "TBL_PAYMENT_LIST":
                    combined = "급여 내역: " + buildNaturalSentence(doc); break;
                case "TBL_REACTION":
                    combined = "게시글 반응: " + buildNaturalSentence(doc); break;
                case "TBL_RECOMMEND":
                    combined = "추천: " + buildNaturalSentence(doc); break;
                case "TBL_REPLY":
                    combined = "댓글 답글: " + buildNaturalSentence(doc); break;
                case "TBL_SIGNLINE":
                    combined = "결재라인 정보: " + buildNaturalSentence(doc); break;
                case "TBL_SIGNLINE_MEMBER":
                    combined = "결재자: " + buildNaturalSentence(doc); break;
                case "TBL_SURVEY":
                    combined = "설문: " + buildNaturalSentence(doc); break;
                case "TBL_SURVEY_RESP":
                    combined = "설문 응답: " + buildNaturalSentence(doc); break;
                case "TBL_SURVEY_TARGET":
                    combined = "설문 대상: " + buildNaturalSentence(doc); break;
                case "TBL_VACATION":
                    combined = "휴가: " + buildNaturalSentence(doc); break;
                default:
                    combined = doc.getTableName() + ": " + buildNaturalSentence(doc);
            }

            // 확인용 출력
            System.out.println("벡터화용: " + combined);

            // 벡터화 후 저장
            float[] embedding = embeddingModel.embed(combined);
            vectorStoreRepository.save(doc, embedding);
        }

        System.out.println("DB 전체 문서 인덱싱 완료. 총 " + allDocuments.size() + "건.");
    }

    // helper: 컬럼과 값을 자연어로 변환
    private String buildNaturalSentence(DocumentDTO doc) {
        return IntStream.range(0, doc.getColumnNames().size())
                .mapToObj(i -> doc.getColumnNames().get(i) + " " + doc.getValues().get(i))
                .collect(Collectors.joining(", "));
    }


    /**
     * 사용자 질문 기반 RAG 챗봇
     */
    public String ragChat(String userQuestion, int memberSeq) {
        // 질문 임베딩
        float[] queryEmbedding = embeddingModel.embed(userQuestion);

        // 벡터 유사도 검색
        List<DocumentDTO> relevantDocs = vectorStoreRepository.search(queryEmbedding, 5);

        // context 문자열 생성
        String context = relevantDocs.stream()
                .map(d -> d.getTableName() + ": " +
                        IntStream.range(0, d.getColumnNames().size())
                                 .mapToObj(i -> d.getColumnNames().get(i) + " " + d.getValues().get(i))
                                 .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n"));

        String prompt = """
                당신은 그룹웨어 CodeON 전문 AI 어시스턴트입니다.
                아래 DB 데이터(context)를 참고하여 질문에 답하세요.
                - 사원/직급/부서 정보는 TBL_MEMBER에서,
                - 게시글/댓글 정보는 TBL_BOARD, TBL_COMMENT에서,
                - 문서/결재 정보는 TBL_DRAFT, TBL_BUSINESS, TBL_SIGNLINE에서,
                - 메일 정보는 TBL_EMAIL에서,
                - 설문은 TBL_SURVEY, TBL_SURVEY_RESP, TBL_SURVEY_TARGET에서,
                - 급여/지급 관련 정보는 TBL_PAYMENT, TBL_PAYMENT_LIST에서,
                - 연차/휴가/출퇴근 관련 정보는 TBL_ANNUAL_LEAVE, TBL_VACATION, TBL_ATTENDANCE에서,
                - 그 외 테이블도 관련 정보를 자연어로 설명하세요.
                답변은 자연어로 요약/설명해 주세요.

                [Context]
                %s

                [질문]
                %s
                """.formatted(context, userQuestion);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }


}
