<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="<%=ctxPath%>/smarteditor/js/HuskyEZCreator.js" charset="utf-8"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="mailsidebar.jsp" />

<script type="text/javascript">
$(function(){
    var oEditors = [];

    nhn.husky.EZCreator.createInIFrame({
        oAppRef: oEditors,
        elPlaceHolder: "emailContent",
        sSkinURI: "<%=ctxPath%>/smarteditor/SmartEditor2Skin.html",
        htParams: {
            bUseToolbar: true,
            bUseVerticalResizer: true,
            bUseModeChanger: true
        }
    });

    $('button#btnWrite').click(function(e){
        oEditors.getById["emailContent"].exec("UPDATE_CONTENTS_FIELD", []);

        // 제목 유효성 검사
        var title = $('input[name="emailTitle"]').val().trim();
        if(title == "") {
            alert("제목을 입력하세요!");
            $('input[name="emailTitle"]').focus();
            return false;
        }

        // 내용 유효성 검사
        var content = $('textarea[name="emailContent"]').val().trim();
        content = content.replace(/&nbsp;/gi, "");
        content = content.replace(/<[^>]*>/gi, "");
        if(content.length == 0) {
            alert("메일 내용을 입력하세요!");
            return false;
        }

        // 받는 사람 유효성 검사 (콤마 구분)
        var emails = $('input[name="receiveMemberEmail"]').val().trim().split(',');
        for(var i=0; i<emails.length; i++){
            var email = emails[i].trim();
            if(email.length == 0) continue; // 빈 값 무시
            var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if(!emailPattern.test(email)){
                alert("유효하지 않은 이메일 주소가 있습니다: " + email);
                return false;
            }
        }

        // 폼 전송
        const frm = document.writeFrm;
        frm.method = "post";
        frm.action = "<%= ctxPath%>/mail/write";
        frm.submit();
    });
});
</script>

<main style="margin-left: 240px; padding-top: 20px;">
    <div class="container-fluid">
        <h3 class="mb-4">메일 작성</h3>

        <form id="mailForm" name="writeFrm" enctype="multipart/form-data">
            <!-- 보내는 사람 -->
            <div class="mb-3 row">
                <label for="sendMemberEmail" class="col-sm-2 col-form-label">보내는 사람</label>
                <div class="col-sm-10">
                    <input type="email" class="form-control" id="sendMemberEmail" name="sendMemberEmail"
                           value="${sessionScope.loginuser.memberEmail}" readonly>
                </div>
            </div>

            <!-- 받는 사람 (여러 명) -->
            <div class="mb-3 row">
                <label for="receiveMemberEmail" class="col-sm-2 col-form-label">받는 사람</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="receiveMemberEmail" name="receiveMemberEmail" 
                           placeholder="여러 명일 경우 ,(콤마)로 구분" required>
                </div>
            </div>

            <!-- 제목 + 중요 -->
            <div class="mb-3 row align-items-center">
                <label for="emailTitle" class="col-sm-2 col-form-label">
                    제목
                    <input type="checkbox" id="importantStatus" name="importantStatus" value="1" class="ms-2">
                    <span class="small">중요 *</span>
                </label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="emailTitle" name="emailTitle" required>
                </div>
            </div>

            <!-- 첨부파일 -->
            <div class="mb-3 row">
                <label for="emailFile" class="col-sm-2 col-form-label">첨부파일</label>
                <div class="col-sm-10">
                    <input type="file" name="attach">
                </div>
            </div>

            <!-- 내용 -->
            <div class="mb-3 row">
                <label for="emailContent" class="col-sm-2 col-form-label">내용</label>
                <div class="col-sm-10">
                    <textarea id="emailContent" name="emailContent"></textarea>
                </div>
            </div>

            <!-- 버튼 -->
            <div class="text-end">
                <button type="button" class="btn btn-primary" id="btnWrite">보내기</button>
                <button type="reset" class="btn btn-secondary">초기화</button>
            </div>
        </form>
    </div>
</main>

<jsp:include page="../footer/footer.jsp" />
