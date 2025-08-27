package com.spring.app.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.board.domain.BoardDTO;
import com.spring.app.board.service.BoardService;
import com.spring.app.common.FileManager;
import com.spring.app.common.MyUtil;
import com.spring.app.domain.MemberDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board/")
public class BoardController {

    private final BoardService boardService;
    private final FileManager fileManager;
    
    // 글쓰기 폼 요청(GET) 
    @GetMapping("add")
    public ModelAndView addForm(@RequestParam(value = "fk_board_type_seq", required = false) Integer fkBoardTypeSeq,
                                HttpSession session,                    
                                ModelAndView mav) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            mav.addObject("message", "로그인 후 이용 가능합니다.");
            mav.addObject("loc", "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }
        if (fkBoardTypeSeq == null) fkBoardTypeSeq = 0;

        List<Map<String, Object>> boardTypeList = boardService.getBoardTypeList();
        List<Map<String, Object>> boardCategoryList = boardService.getBoardCategoryList();

        mav.addObject("boardTypeList", boardTypeList);
        mav.addObject("boardCategoryList", boardCategoryList);
        mav.addObject("fk_board_type_seq", fkBoardTypeSeq);
        mav.addObject("loginuser", loginuser);
        mav.setViewName("board/add");
        return mav;	
    }

    // 글쓰기 처리(POST) 
    @PostMapping("add")
    public ModelAndView addPost(BoardDTO boardDto, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        try {
            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
            boardDto.setFkMemberSeq(loginuser.getMemberSeq());
            boardDto.setMemberName(loginuser.getMemberName());
            
            // ===== 파일 업로드 처리 =====
            if (boardDto.getAttach() != null && !boardDto.getAttach().isEmpty()) {
                String originalFilename = boardDto.getAttach().getOriginalFilename();
                boardDto.setBoardFileOriName(originalFilename);

                String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                boardDto.setBoardFileSaveName(savedFilename);

                boardDto.setBoardFileSize(boardDto.getAttach().getSize());

                String uploadDir = session.getServletContext().getRealPath("/resources/upload");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File savedFile = new File(dir, savedFilename);
                boardDto.getAttach().transferTo(savedFile);
            }

            System.out.println("fkBoardTypeSeq = " + boardDto.getFkBoardTypeSeq());

            boardService.add(boardDto);

            // redirect 시 camelCase 일관성 유지
            mav.setViewName("redirect:/board/list?fkBoardTypeSeq=" + boardDto.getFkBoardTypeSeq());

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


    // 게시물 목록
    @GetMapping("list")
    public ModelAndView list(ModelAndView mav, HttpServletRequest request, 
                             @RequestParam(name="searchType", defaultValue="") String searchType,
                             @RequestParam(name="searchword", defaultValue="") String searchword, 
                             @RequestParam(name="currentShowPageNo", defaultValue="1") String currentShowPageNo, 
                             @RequestParam(name="fkBoardCategorySeq",defaultValue="") String  fkBoardCategorySeq,
                             @RequestParam(name="fkBoardTypeSeq",defaultValue="0")String fkBoardTypeSeq,
                             HttpServletResponse response) {

        List<BoardDTO> boardList = null;
        
        HttpSession session = request.getSession();
        session.setAttribute("readCountPermission", "yes");
        
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginuser");
        Integer userDept = (loginUser != null) ? loginUser.getFkDepartmentSeq() : null;
        
        // ---- 로그인 유저 부서명 추가 ----
        String loginUserDeptName = "부서없음";
        if (loginUser != null && loginUser.getDepartment() != null) {
            loginUserDeptName = loginUser.getDepartment().getDepartmentName();
        } 
        mav.addObject("loginUserDeptName", loginUserDeptName);
        
        
        
        String referer = request.getHeader("Referer");
        if(referer == null) {
            mav.setViewName("redirect:/index");
            return mav;
        }

        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("searchType", searchType);
        paraMap.put("searchWord", searchword);
        paraMap.put("fkBoardCategorySeq", fkBoardCategorySeq);
        paraMap.put("fkBoardTypeSeq", fkBoardTypeSeq);
        
        // if fkBoardTypeSeq="1" (부서게시판) 일때 현재 로그인중인 유저의 부서를 걸러내는 용도
        if("1".equals(fkBoardTypeSeq) && userDept != null) {
            paraMap.put("fkDepartmentSeq", String.valueOf(userDept));
        }
        
        int totalCount = boardService.getTotalCount(paraMap);
        int sizePerPage = 10;  
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);

        int pageNo = 1; 
        try {
            pageNo = Integer.parseInt(currentShowPageNo);
            if(pageNo < 1 || pageNo > totalPage) {
                pageNo = 1;
            }
        } catch (NumberFormatException e) {
            pageNo = 1;
        }

        int startRno = ((pageNo - 1) * sizePerPage) + 1;
        int endRno = startRno + sizePerPage - 1;

        paraMap.put("sizePerPage", String.valueOf(sizePerPage));
        paraMap.put("startRno", String.valueOf(startRno));
        paraMap.put("endRno", String.valueOf(endRno));

        boardList = boardService.boardListSearch_withPaging(paraMap);

        mav.addObject("boardList", boardList);

        if(!"".equals(searchType)) {
            mav.addObject("paraMap", paraMap);
        }

        int blockSize = 10;
        int loop = 1;
        int pageStart = ((pageNo - 1) / blockSize) * blockSize + 1;

        String pageBar = "<ul style='list-style:none;'>";
        String url = "list";

        pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchword="+searchword+"&currentShowPageNo=1'>[맨처음]</a></li>"; 

        if(pageStart != 1) {
            pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchword="+searchword+"&currentShowPageNo="+(pageStart-1)+"'>[이전]</a></li>"; 
        }

        while(!(loop > blockSize || pageStart > totalPage)) {
            if(pageStart == pageNo) {
                pageBar += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageStart+"</li>";
            }
            else {
                pageBar += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchword="+searchword+"&currentShowPageNo="+pageStart+"'>"+pageStart+"</a></li>"; 
            }
            loop++;
            pageStart++;
        }

        if(pageStart <= totalPage) {
            pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchword="+searchword+"&currentShowPageNo="+pageStart+"'>[다음]</a></li>";  
        }

        pageBar += "<li style='display:inline-block; width:70px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchword="+searchword+"&currentShowPageNo="+totalPage+"'>[마지막]</a></li>"; 
        pageBar += "</ul>";

        mav.addObject("pageBar", pageBar);
        
        mav.addObject("totalCount", totalCount);
        mav.addObject("currentShowPageNo", pageNo);
        mav.addObject("sizePerPage", sizePerPage);

        String listURL = MyUtil.getCurrentURL(request);
        Cookie cookie = new Cookie("listURL", listURL);
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/myspring/board/");
        response.addCookie(cookie);

        mav.setViewName("board/list"); 
        return mav;
    }

    @GetMapping("view")
    public String view(@RequestParam("boardSeq") String boardSeq,
                       Model model,
                       RedirectAttributes redirectAttrs) {//RedirectAttributes는 리다이렉트시 메시지를 "flash" 속성으로 넘겨줄 때 달아준다
        // 게시글 상세 조회
    	BoardDTO board = boardService.getBoardDetail(boardSeq);

        if (board == null) {
            // 존재하지 않는 게시글일 경우 리스트로 리다이렉트
            redirectAttrs.addFlashAttribute("errorMsg", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }

        // 댓글 목록 조회 (추후 구현)
        // List<CommentDTO> commentList = commentService.getCommentList(boardSeq);

        // 리액션 집계 (추후 구현)
        // ReactionCountDTO reaction = reactionService.getReactionCount(boardSeq);

        //이전/다음 글 조회
        BoardDTO prevBoard = boardService.getPrevBoard(boardSeq);
        BoardDTO nextBoard = boardService.getNextBoard(boardSeq);

        // JSP로 데이터 전달
        model.addAttribute("board", board);
        // model.addAttribute("commentList", commentList);
        // model.addAttribute("reaction", reaction);
        model.addAttribute("prevBoard", prevBoard);
        model.addAttribute("nextBoard", nextBoard);

        return "board/view"; // view.jsp
    }
 // 1. 글 수정 페이지 이동(GET)
    @GetMapping("edit")
    public ModelAndView editForm(@RequestParam("boardSeq") String boardSeq,
                                 HttpSession session) {
        ModelAndView mav = new ModelAndView();

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            mav.addObject("message", "로그인 후 이용 가능합니다.");
            mav.addObject("loc", "/login/loginStart");
            mav.setViewName("msg");
            return mav;
        }

        BoardDTO board = boardService.getBoardDetail(boardSeq);
        if (board == null) {
            mav.addObject("message", "존재하지 않는 게시글입니다.");
            mav.addObject("loc", "/board/list");
            mav.setViewName("msg");
            return mav;
        }

        // 작성자 체크
        if (loginuser.getMemberSeq() != board.getFkMemberSeq()) {
            mav.addObject("message", "본인 글만 수정 가능합니다.");
            mav.addObject("loc", "/board/view?boardSeq=" + boardSeq);
            mav.setViewName("msg");
            return mav;
        }

        // 게시판 타입/카테고리
        List<Map<String, Object>> boardTypeList = boardService.getBoardTypeList();
        List<Map<String, Object>> boardCategoryList = boardService.getBoardCategoryList();

        mav.addObject("board", board);
        mav.addObject("boardTypeList", boardTypeList);
        mav.addObject("boardCategoryList", boardCategoryList);
        mav.addObject("loginuser", loginuser);
        mav.setViewName("board/edit"); // edit.jsp 호출
        return mav;
    }

    // 2. 글 수정 처리(POST)
    @PostMapping("edit")
    public ModelAndView editPost(BoardDTO boardDto, HttpSession session) {
        ModelAndView mav = new ModelAndView();

        try {
            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
            if (loginuser == null || loginuser.getMemberSeq() != boardDto.getFkMemberSeq()) {
                mav.addObject("message", "본인 글만 수정 가능합니다.");
                mav.addObject("loc", "/board/list");
                mav.setViewName("msg");
                return mav;
            }

            // ===== 파일 업로드 처리 =====
            if (boardDto.getAttach() != null && !boardDto.getAttach().isEmpty()) {
                String originalFilename = boardDto.getAttach().getOriginalFilename();
                boardDto.setBoardFileOriName(originalFilename);

                String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                boardDto.setBoardFileSaveName(savedFilename);

                boardDto.setBoardFileSize(boardDto.getAttach().getSize());

                String uploadDir = session.getServletContext().getRealPath("/resources/upload");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                File savedFile = new File(dir, savedFilename);
                boardDto.getAttach().transferTo(savedFile);

                // 기존 첨부파일 삭제
                BoardDTO oldBoard = boardService.getBoardDetail(String.valueOf(boardDto.getBoardSeq()));
                if (oldBoard.getBoardFileSaveName() != null && !oldBoard.getBoardFileSaveName().isEmpty()) {
                    File oldFile = new File(uploadDir, oldBoard.getBoardFileSaveName());
                    if (oldFile.exists()) oldFile.delete();
                }
            }

            boardService.updateBoard(boardDto);

            mav.setViewName("redirect:/board/view?boardSeq=" + boardDto.getBoardSeq());

        } catch (IOException e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/edit");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "글 수정 중 오류가 발생했습니다.");
            mav.addObject("boardDto", boardDto);
            mav.setViewName("board/edit");
        }

        return mav;
    }  
    
    
    
 // 게시글 삭제 
    @PostMapping("delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam("boardSeq") String boardSeq,
                                      HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) {
            result.put("status", "fail");
            result.put("message", "로그인 후 사용 가능합니다.");
            return result;
        }
        
        BoardDTO board = boardService.getBoardDetail(boardSeq);
        if (board == null) {
            result.put("status", "fail");
            result.put("message", "존재하지 않는 게시글입니다.");
            return result;
        }
        
        // 작성자만 삭제 가능
        if (loginuser.getMemberSeq() != board.getFkMemberSeq()) {
            result.put("status", "fail");
            result.put("message", "본인 글만 삭제 가능합니다.");
            return result;
        }
        
        try {
            // 첨부파일 삭제
            if (board.getBoardFileSaveName() != null && !board.getBoardFileSaveName().isEmpty()) {
                String uploadDir = session.getServletContext().getRealPath("/resources/upload");
                File file = new File(uploadDir, board.getBoardFileSaveName());
                if (file.exists()) file.delete();
            }
            
            // DB 삭제 (댓글/대댓글테이블 시퀀스에 제약조건 DELETE CASCADE 처리해놓음)
            boardService.delete(boardSeq);
            
            result.put("status", "success");
            result.put("message", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "fail");
            result.put("message", "삭제 중 오류가 발생했습니다.");
        }
        
        return result;
    }

    
}