<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   String ctxPath = request.getContextPath();
%>      
<link rel="stylesheet" href="<%= ctxPath %>/bootstrap-4.6.2-dist/css/bootstrap.min.css" type="text/css">
<jsp:include page="../header/header.jsp" />
<jsp:include page="adminsidebar.jsp" />

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>사원 등록</title>

<script type="text/javascript">
$(function() {
	
	// 모든 회원정보
	allMember();
}); // end of $(function() {})--------

//Function Declaration
//모든 회원정보 출력
function allMember() {
	$.ajax({
		url:"<%= ctxPath%>/memberInfo/allMember",
        dataType:"json",
        success: function(json){
        	console.log(JSON.stringify(json));
        	
        	let v_html = `<table class='table table-striped'>
                <thead>
                  <th>입사일</th>
                  <th>사원번호</th>
                  <th>부서</th>
                  <th>직급</th>
                  <th>이메일</th>
                </thead>
                    <tbody>`;
        	
        	if (json.length == 0) {
        		v_html += `<tr>
                    		<td colspan='5' align='center'>가입된 회원이 없습니다.</td>    
                 		 </tr>`;
        	} else {
        		$.each(json, function(index, item) {
        			const v_gender = item.gender == 1 ? '남' : '여';
        			v_html += `<tr style='cursor: pointer;'>
                        <td class='userinfo' width='25%'>\${item.userId}</td>
                        <td class='userinfo' width='25%'>\${item.userName}</td>
                        <td class='userinfo' width='25%'>\${item.member}</td>
                        <td class='userinfo' width='25%'>\${item.memberEmail}</td>
                      </tr>`;
        		});
        	}
        	
        	v_html += `</tbody>
        			</table>`;
        			
        	$('div#tbl_1').html(v_html);
        },
        error: function(request, status, error){
            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
        } 
	});
}; // end of function allMember() {}---------
</script>

<jsp:include page="../footer/footer.jsp" />

<body>
<br><br>
   <div class="container" style="margin-left: 220px;">
     <div class="row pt-5">
      <div class="col-md-8">
         <p class="h2 text-center text-muted">회원정보</p>
         <div id="tbl_1" class="mt-4"></div>
         
         <form name="searchFrm">
            <ul style="list-style-type: none; padding: 0; border: solid 0px blue;">
              <li style="display: inline-block; border: solid 0px green; width:35%;">
                <select name="select" style="width: 20%; height: 35px;">
                   <option value="">선택</option>
                   <option value="1">사번</option>
                   <option value="2">이름</option>
                </select>
                <input type="text" name="userName" width="40%" />
                <input type="text" style="display: none;" />
              </li>
              <li style="display: inline-block; width:60%;">
                <select name="gender" style="width: 20%; height: 35px;">
                   <option value="">성별선택</option>
                   <option value="1">남</option>
                   <option value="2">여</option>
                </select>
                <button type="button" class="btn btn-danger ml-2" onclick="goSearch()">회원검색</button>
              </li>
            </ul>
         </form>
      </div>
     </div>
   </div>
</body>
