package com.spring.app.mail.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;
import com.spring.app.mail.domain.MailDTO;
import com.spring.app.mail.domain.MailUserStatusDTO;
import com.spring.app.mail.service.MailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailScheduler {

    private final MailService mailService;
    private final BoardService boardService;
    
    // 1분마다 실행
    @Scheduled(cron = "0 0 * * * *") // 매 분 0초마다 실행
    public void sendScheduledMail() {
        int fkBoardTypeSeq = 0; // 예: 사내게시판
        Integer fkDepartmentSeq = null; // 전체
        
        // DB에서 이번주 인기글 조회
        List<BoardDTO> popularBoards = boardService.getWeeklyPopularBoard(fkBoardTypeSeq, fkDepartmentSeq);

        // 메일 내용 구성
        StringBuilder content = new StringBuilder();
        content.append("현재 인기글 목록:\n\n");
        for (BoardDTO b : popularBoards) {
            content.append("제목: ").append(b.getBoardTitle())
                   .append(" | 조회수: ").append(b.getBoardReadcount())
                   .append(" | 추천수: ").append(b.getRecommendCount())
                   .append("\n");
        }

        // MailDTO 구성
        MailDTO mail = MailDTO.builder()
                .sendMemberEmail("park@CodeON.com")
                .receiveMemberEmail("leess@CodeON.com")
                .emailTitle("현재 인기글 목록")
                .emailContent(content.toString())
                .build();

        // 수신자/발신자 상태 설정
        List<MailUserStatusDTO> statusList = new ArrayList<>();
        statusList.add(MailUserStatusDTO.builder()
                .memberEmail(mail.getSendMemberEmail())
                .readStatus("1")
                .importantStatus("0")
                .build());
        for (String rEmail : mail.getReceiveMemberEmail().split(",")) {
            if (rEmail.isEmpty()) continue;
            statusList.add(MailUserStatusDTO.builder()
                    .memberEmail(rEmail)
                    .readStatus("0")
                    .importantStatus("0")
                    .build());
        }
        mail.setUserStatusList(statusList);

        // 메일 DB 저장
        mailService.write(mail);

        System.out.println("자동 발송 메일 완료: " + mail.getEmailTitle());
    }
}
