// src/main/java/com/spring/app/domain/SurveyStatsDTO.java
package com.spring.app.domain;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SurveyStatsDTO {
    private Long surveyId;
    private int totalResponses;          // 설문 전체 응답 수
    private List<QuestionStatDTO> questions; // 문항별 집계
}
