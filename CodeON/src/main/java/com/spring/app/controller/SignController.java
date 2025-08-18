package com.spring.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.common.FileManager;
import com.spring.app.domain.MemberDTO;
import com.spring.app.domain.SignlineDTO;
import com.spring.app.entity.Signline;
import com.spring.app.entity.SignlineMember;
import com.spring.app.model.MemberRepository;
import com.spring.app.model.SignlineRepository;
import com.spring.app.service.MemberService;
import com.spring.app.service.SignlineService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sign")   // ★ 중요: /sign/* -> /sign 로 변경
public class SignController {

    private final FileManager fileManager;
    private final MemberRepository memberRepository;
    private final SignlineRepository signlineRepository;
    
    private final SignlineService signlineService;
    private final MemberService memberService;

    @GetMapping("main")
    public String signmain() {
        return "/sign/signmain";
    }
    
    @GetMapping("add")
    public String signadd() {

        return "/sign/signadd";
    }

    @GetMapping("setting")
    public String signsetting(HttpSession session, HttpServletRequest request, Model model) {

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser != null) {
            String savedFilename = memberRepository.findStampImageByUserid(loginuser.getMemberUserid());
            if (savedFilename != null && !savedFilename.isBlank()) {
                String ctxPath = request.getContextPath();
                String stampUrl = ctxPath + "/resources/stamp_upload/" + savedFilename;

                model.addAttribute("stampFilename", savedFilename);
                model.addAttribute("stampUrl", stampUrl);
            }
        }

        return "/sign/signsetting";
    }

    @PostMapping("stampImageSave")
    public void stampImageSave(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String root = session.getServletContext().getRealPath("/");
        String path = root + "resources" + File.separator + "stamp_upload";

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            String filename = request.getHeader("file-name"); // 원본 파일명
            InputStream is = request.getInputStream();

            String newFilename = fileManager.doFileUpload(is, filename, path);

            MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
            memberRepository.stampImageSave(loginuser.getMemberUserid(), newFilename);

            String ctxPath = request.getContextPath();
            String fileUrl = ctxPath + "/resources/stamp_upload/" + newFilename;

            response.setContentType("application/json;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.print("{\"result\":\"success\", \"url\":\"" + fileUrl + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print("{\"result\":\"fail\"}");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @PostMapping("stampImageDelete")
    @ResponseBody
    @Transactional
    public void stampImageDelete(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");

        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            if (loginuser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"result\":\"fail\",\"reason\":\"unauth\"}");
                return;
            }

            String userid = loginuser.getMemberUserid();

            String savedFilename = memberRepository.findStampImageByUserid(userid);

            if (savedFilename != null && !savedFilename.isBlank()) {
                String root = session.getServletContext().getRealPath("/");
                String path = root + "resources" + File.separator + "stamp_upload";
                try {
                    fileManager.doFileDelete(savedFilename, path);
                } catch (Exception ignore) {}
            }

            memberRepository.clearStampImageByUserid(userid);

            out.print("{\"result\":\"success\"}");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.getWriter().print("{\"result\":\"fail\"}");
            } catch (IOException ignore) {}
        }
    }

    // 결재라인 팝업 (id 있으면 상세 조회해서 모델에 담음)
    @GetMapping("setting/line")
    public String linePopup(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("signline", signlineService.findDetail(id));
        }
        return "sign/signlinepopup";
    }

    // 좌측 목록: 부서/직원 리스트
    @GetMapping("members")
    @ResponseBody
    public List<MemberDTO> members() {
        return memberService.getSignlineMember();
    }
    
    @PostMapping("lines/write")
    @Transactional
    public void saveLine(
            @RequestParam String lineName,
            @RequestParam("approverSeq") List<Long> approverSeqs,
            @RequestParam(value="id", required=false) Long id,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        if (loginuser == null) { // 로그인 안됨 → 로그인 페이지로
            response.sendRedirect(request.getContextPath() + "/login/loginStart");
            return;
        }

        if (lineName == null || lineName.isBlank() || approverSeqs == null || approverSeqs.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign/setting");
            return;
        }

        // === 저장/수정 ===
        if (id == null) {
            // 신규
            Signline line = Signline.builder()
                    .fkMemberSeq(Long.valueOf(loginuser.getMemberSeq()))
                    .signlineName(lineName)
                    .build();

            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .memberSeq(mseq.intValue())   // SignlineMember.memberSeq가 Integer 타입
                        .lineOrder(order++)
                        .build());
            }
            signlineRepository.save(line); // cascade로 멤버도 함께 INSERT
        } else {
            // 수정
            Signline line = signlineRepository.findById(id).orElseThrow();
            line.setSignlineName(lineName);
            line.getMembers().clear();     // orphanRemoval=true라 기존 멤버 DELETE
            int order = 1;
            for (Long mseq : approverSeqs) {
                line.addMember(SignlineMember.builder()
                        .memberSeq(mseq.intValue())
                        .lineOrder(order++)
                        .build());
            }
            signlineRepository.save(line); // 넣어도 되고, 더티체킹으로 생략해도 됨
        }

        // === 팝업이면 부모 새로고침 후 닫기, 팝업이 아니면 설정 화면으로 이동 ===
        String ctx = request.getContextPath();
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><body><script>");
            out.println("if (window.opener && !window.opener.closed) {");
            out.println("  try { if (window.opener.loadSavedLines) window.opener.loadSavedLines(); } catch(e) {}");
            out.println("  window.close();");
            out.println("} else {");
            out.println("  window.location.replace('" + ctx + "/sign/setting');");
            out.println("}");
            out.println("</script></body></html>");
        }
    }

    @GetMapping("lines")
    @ResponseBody
    public List<SignlineDTO> lines(HttpSession session) {
        MemberDTO loginuser = (MemberDTO) session.getAttribute("loginuser");
        // 로그인 사용자의 결재라인 목록 반환
        return signlineService.findAllByOwner(loginuser.getMemberSeq());
    }
    
}
