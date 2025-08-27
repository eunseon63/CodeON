package com.spring.app.ai.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.spring.app.domain.MemberDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final ChatClient chatClient;

    
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
		
		return chatClient.prompt() // 프롬프트 생성
				.user(prompt) 	   // 사용자 메시지
				.call() 		   // 호출
				.content(); 	   // 요청정보를 받는 부분
	}



    
}
