<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="../header/header.jsp" />

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- SmartEditor 2 -->
<script type="text/javascript" src="<%= ctxPath %>/smarteditor/js/HuskyEZCreator.js" charset="utf-8"></script>
<script type="text/javascript">
$(function(){
    var obj = [];

    nhn.husky.EZCreator.createInIFrame({
        oAppRef: obj,
        elPlaceHolder: "board_content",
        sSkinURI: "<%= ctxPath%>/smarteditor/SmartEditor2Skin.html",
        htParams : {
            bUseToolbar : true,
            bUseVerticalResizer : true,
            bUseModeChanger : true
        }
    });

    $("#btnWrite").click(function(){
        obj.getById["board_content"].exec("UPDATE_CONTENTS_FIELD", []);

        const title = $('input[name="board_title"]').val().trim();
        if(title === "") {
            alert("제목을 입력하세요.");
            return;
        }

        let contentVal = $('textarea[name="board_content"]').val().trim();
        contentVal = contentVal.replace(/&nbsp;/gi, "");
        contentVal = contentVal.substring(contentVal.indexOf("<p>")+3);
        contentVal = contentVal.substring(0, contentVal.indexOf("</p>"));
        if(contentVal.trim().length === 0) {
            alert("내용을 입력하세요.");
            return;
        }

        const pw = $('input[name="board_password"]').val();
        if(pw === "") {
            alert("글 암호를 입력하세요.");
            return;
        }

        const frm = document.addFrm;
        frm.method = "post";
        frm.action = "<%= ctxPath%>/board/add";
        frm.submit();
    });
});
</script>

<div style="display: flex;">
    <div style="margin: auto; padding-left: 3%;">
        <h2 style="margin-bottom: 30px;">글쓰기</h2>
       <form name="addFrm" enctype="multipart/form-data" method="post" action="${pageContext.request.contextPath}/board/add">
            <table style="width: 1024px" class="table table-bordered">
                <tr>
                    <th style="width: 15%; background-color: #DDDDDD;">작성자</th>
                    <td>
                        <!-- fk_member_seq -->
                        <input type="hidden" name="fk_member_seq" value="${sessionScope.loginUser.memberSeq}" />
                        <input type="text" name="member_name" value="${sessionScope.loginUser.memberName}" readonly />
                    </td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">게시판 타입</th>
                    <td>
                        <!-- fk_board_type_seq -->
                        <input type="number" name="fk_board_type_seq" value="1" />
                    </td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">카테고리</th>
                    <td>
                        <!-- fk_board_category_seq -->
                        <input type="number" name="fk_board_category_seq" value="1" />
                    </td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">제목</th>
                    <td><input type="text" name="board_title" size="100" maxlength="200" /></td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">내용</th>
                    <td><textarea style="width: 100%; height: 500px;" name="board_content" id="board_content"></textarea></td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">파일첨부</th>
                    <td><input type="file" name="attach" /></td>
                </tr>
                <tr>
                    <th style="background-color: #DDDDDD;">글암호</th>
                    <td><input type="password" name="board_password" maxlength="100" /></td>
                </tr>
            </table>
            <div style="margin: 20px;">
                <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnWrite">글쓰기</button>
                <button type="button" class="btn btn-secondary btn-sm" onclick="history.back()">취소</button>
            </div>
        </form>
    </div>
</div>
