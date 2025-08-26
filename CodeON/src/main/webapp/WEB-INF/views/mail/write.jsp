<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="mailsidebar.jsp" />

<script type="text/javascript" src="${ctxPath}/resources/smarteditor2/js/service/HuskyEZCreator.js"></script>
<script type="text/javascript">
   $(function(){
       var oEditors = [];

       nhn.husky.EZCreator.createInIFrame({
           oAppRef: oEditors,
           elPlaceHolder: "emailContent",
           sSkinURI: "${ctxPath}/smarteditor/SmartEditor2Skin.html",
           htParams: {
               bUseToolbar: true,
               bUseVerticalResizer: true,
               bUseModeChanger: true
           }
       });

       $('#mailForm').submit(function(e){
           oEditors.getById["emailContent"].exec("UPDATE_CONTENTS_FIELD", []);

           // 제목 유효성 검사
           var title = $('input[name="emailTitle"]').val().trim();
           if(title == "") {
               alert("제목을 입력하세요!");
               $('input[name="emailTitle"]').focus();
               e.preventDefault();
               return false;
           }

           // 본문 유효성 검사
           var content = $('textarea[name="emailContent"]').val().trim();
           content = content.replace(/&nbsp;/gi, "");
           content = content.replace(/<[^>]*>/gi, "");
           if(content.length == 0) {
               alert("메일 내용을 입력하세요!");
               e.preventDefault();
               return false;
           }

           return true;
       });
   });
</script>

<main style="margin-left: 240px; padding-top: 20px;">
    <div class="container-fluid">
        <h3 class="mb-4">메일 작성</h3>

        <form id="mailForm" method="post" enctype="multipart/form-data" action="${ctxPath}/mail/send">
            <div class="mb-3 row">
                <label for="receiveMemberEmail" class="col-sm-2 col-form-label">받는 사람</label>
                <div class="col-sm-10">
                    <input type="email" class="form-control" id="receiveMemberEmail" name="receiveMemberEmail" required>
                </div>
            </div>

            <div class="mb-3 row">
                <label for="emailTitle" class="col-sm-2 col-form-label">제목</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="emailTitle" name="emailTitle" required>
                </div>
            </div>

            <div class="mb-3 row">
                <label for="emailContent" class="col-sm-2 col-form-label">내용</label>
                <div class="col-sm-10">
                    <textarea id="emailContent" name="emailContent"></textarea>
                </div>
            </div>

            <div class="mb-3 row">
                <label for="emailFile" class="col-sm-2 col-form-label">첨부파일</label>
                <div class="col-sm-10">
                    <input type="file" class="form-control" id="emailFile" name="emailFile">
                </div>
            </div>

            <div class="text-end">
                <button type="submit" class="btn btn-primary">보내기</button>
                <button type="reset" class="btn btn-secondary">취소</button>
            </div>
        </form>
    </div>
</main>

<jsp:include page="../footer/footer.jsp" />
