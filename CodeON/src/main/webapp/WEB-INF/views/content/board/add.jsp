<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<%
    String ctxPath = request.getContextPath();
%>   
<jsp:include page="/WEB-INF/views/header/header.jsp" />

<script type="text/javascript">
   $(function(){
	   
	   // 스마트 에디터 전역 변수
	   var oEditors = [];
	   
	   // 스마트에디터 프레임 생성
	   nhn.husky.EZCreator.createInIFrame({
	       oAppRef: oEditors,
	       elPlaceHolder: "content",
	       sSkinURI: "<%= ctxPath %>/smarteditor/SmartEditor2Skin.html",
	       htParams: {
	           bUseToolbar: true,
	           bUseVerticalResizer: true,
	           bUseModeChanger: true
	       }
	   });
	   
	   // 글쓰기 버튼 클릭 이벤트
	   $('button#btnWrite').click(function() {
	       
	       // 스마트에디터 textarea에 내용 업데이트
	       oEditors[0].exec("UPDATE_CONTENTS_FIELD", []);
	       
	       
	       // 폼 전송
	       const frm = document.addFrm;
	       frm.method = "post";
	       frm.action = "<%= ctxPath %>/board/add";
	       frm.submit();
	   });
   });
</script>

<div style="display: flex;">
   <div style="margin: auto; padding-left: 3%;">
      
      <%-- 제목 부분 (원글/답변 구분 없이 기본 텍스트박스) --%>
      <h2 style="margin-bottom: 30px;">글쓰기</h2>

      <<form name="addFrm" enctype="multipart/form-data">
   <table style="width: 1024px" class="table table-bordered">
       <tr>
          <th style="width: 15%; background-color: #DDDDDD;">성명</th>
          <td>
             <input type="hidden" name="fk_Member_Seq" value="${sessionScope.loginuser.userid}" />
             <input type="text" name="name" value="${sessionScope.loginuser.name}" readonly />
          </td>    
       </tr>
       
       <tr>
          <th style="width: 15%; background-color: #DDDDDD;">제목</th>
          <td>
             <input type="text" name="board_Title" size="100" maxlength="200" value="${requestScope.subject != null ? requestScope.subject : ''}" />
          </td>
       </tr>
       
       <tr>
          <th style="width: 15%; background-color: #DDDDDD;">내용</th> 
          <td>
             <textarea style="width: 100%; height: 612px;" name="board_Content" id="content"></textarea>
          </td>
       </tr> 
       
       <tr>
          <th style="width: 15%; background-color: #DDDDDD;">파일첨부</th> 
          <td>
             <input type="file" name="attach" />
          </td>
       </tr>  
       
       <tr>
          <th style="width: 15%; background-color: #DDDDDD;">글암호</th> 
          <td>
             <input type="password" name="board_Pw" maxlength="20" />
          </td>
       </tr>  
   </table>
   
   <div style="margin: 20px;">
      <button type="button" class="btn btn-secondary btn-sm mr-3" id="btnWrite">글쓰기</button>
      <button type="button" class="btn btn-secondary btn-sm" onclick="history.back();">취소</button>  
   </div>
</form>
   </div>
</div>
