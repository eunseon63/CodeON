package com.spring.app.board.controller;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/")
public class BoardController {

    private final BoardService boardService;

    /** 글쓰기 폼 요청(GET) */
    @GetMapping("add")
    public ModelAndView addForm(@RequestParam(value = "fk_board_type_seq", required = false) Integer fkBoardTypeSeq,
                                ModelAndView mav) {

        if (fkBoardTypeSeq == null) fkBoardTypeSeq = 0;

        mav.addObject("fk_board_type_seq", fkBoardTypeSeq);
        mav.setViewName("board/add");
        return mav;
    }

    /** 글쓰기 처리(POST) */
    @PostMapping("add")
    public ModelAndView addPost(BoardDTO boardDto, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        try {
            
            // ===== 파일 업로드 처리 =====
            if (boardDto.getAttach() != null && !boardDto.getAttach().isEmpty()) {

                // 원본 파일명
                String originalFilename = boardDto.getAttach().getOriginalFilename();
                boardDto.setBoard_file_ori_name(originalFilename);

                // 저장 파일명 (UUID로 중복 방지)
                String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                boardDto.setBoard_file_save_name(savedFilename);

                // 파일 크기
                boardDto.setBoard_file_size(boardDto.getAttach().getSize());

                // 저장 경로
                String uploadDir = session.getServletContext().getRealPath("/upload/board");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // 실제 파일 저장
                File savedFile = new File(dir, savedFilename);
                boardDto.getAttach().transferTo(savedFile);
            }

            // DB 저장
            boardService.add(boardDto);

            mav.setViewName("redirect:/board/list?fk_board_type_seq=" + boardDto.getFk_board_type_seq());

        } catch (IOException e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/add");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "글 작성 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/add");
        }
        return mav;
    }

    /** 게시물 목록 */
    @GetMapping("list")
    public String list(@RequestParam Map<String, String> paramMap, Model model) {
        List<BoardDTO> boardList = boardService.selectBoardList(paramMap);
        model.addAttribute("boardList", boardList); 
        return "board/list";
    }
}
