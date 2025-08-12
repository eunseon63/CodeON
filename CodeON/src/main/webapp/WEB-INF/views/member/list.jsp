<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   String ctxPath = request.getContextPath();
%>      
<jsp:include page="../header/header.jsp" />

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>사원 등록</title>

<link rel="stylesheet" href="<%= ctxPath %>/bootstrap-4.6.2-dist/css/bootstrap.min.css" type="text/css">

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>
<script src="<%= ctxPath %>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript">
$(function() {
    // 페이지 로드 시 모든 회원정보 출력
    allMember();
});

//Function Declaration
// 모든 회원정보 출력 함수
function allMember() {
    $.ajax({
        url:"<%= ctxPath%>/memberInfo/allMember",
        dataType:"json",
        success: function(json){
            let v_html = "";

            if (json.length == 0) {
                v_html = `<tr><td colspan='5' align='center'>가입된 회원이 없습니다.</td></tr>`;
            } else {
                $.each(json, function(index, item) {
                    v_html += `<tr style='cursor: pointer;'>
                                  <td>\${item.memberHiredate}</td>
                                  <td>\${item.memberSeq}</td>
                                  <td>\${
                                    item.fkDepartmentSeq === 10 ? '인사팀' :
                                    item.fkDepartmentSeq === 20 ? '개발팀' :
                                    item.fkDepartmentSeq === 30 ? '개발팀' :
                                    item.fkDepartmentSeq === 40 ? '영업팀' :
                                    item.fkDepartmentSeq === 50 ? '고객지원팀' : ''
                                  }</td>
                                  <td>\${item.memberName}</td>
                                  <td>\${item.memberEmail}</td>
                              </tr>`;
                });
            }

            $('#memberTableBody').html(v_html);
        },
        error: function(request, status, error){
            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
        } 
    });
}

function goSearch() {
	
    // 1. 검색 조건 가져오기 (예시: 실제 id/name에 맞게 수정하세요)
    let searchType = $('#searchType').val() || '';
    let searchWord = $('#searchWord').val() || '';
    let gender = $('#gender').val() || '';

    $.ajax({
        url: "<%= ctxPath %>/memberInfo/searchMember",
        method: "GET",
        dataType: "json",
        data: {
            searchType: searchType,
            searchWord: searchWord,
            gender: gender
        },
        success: function(json) {
        	console.log(JSON.stringify(json));
            let v_html = "";

            if (json.length == 0) {
                v_html = `<tr><td colspan='5' align='center'>검색 결과가 없습니다.</td></tr>`;
            } else {
                $.each(json, function(index, item) {
                    v_html += `<tr style='cursor: pointer;'>
                                  <td>\${item.memberHiredate}</td>
                                  <td>\${item.memberSeq}</td>
                                  <td>\${
                                    item.fkDepartmentSeq === 10 ? '인사팀' :
                                    item.fkDepartmentSeq === 20 ? '개발팀' :
                                    item.fkDepartmentSeq === 30 ? '개발팀' :
                                    item.fkDepartmentSeq === 40 ? '영업팀' :
                                    item.fkDepartmentSeq === 50 ? '고객지원팀' : ''
                                  }</td>
                                  <td>\${item.memberName}</td>
                                  <td>\${item.memberEmail}</td>
                              </tr>`;
                });
            }

            $('#memberTableBody').html(v_html);
        },
        error: function(request, status, error) {
            alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
        }
    });
}; // end of function goSearch()--------------------
</script>

<jsp:include page="../footer/footer.jsp" />

<body>
<br><br><br>
   <div class="container">
     <div class="row pt-5">
      <div class="col-md-8">
         <p class="h2 text-center text-muted">회원정보</p>

         <!-- 테이블 골격은 고정 -->
         <table class='table table-striped'>
           <thead>
             <tr>
               <th>입사일</th>
               <th>사원번호</th>
               <th>부서</th>
               <th>이름</th>
               <th>이메일</th>
             </tr>
           </thead>
           <tbody id="memberTableBody">
             <!-- AJAX 호출 시 여기 tbody 내부에 tr들이 동적으로 들어감 -->
           </tbody>
         </table>

         <form name="searchFrm">
            <ul style="list-style-type: none; padding: 0; border: solid 0px blue;">
              <li style="display: inline-block; border: solid 0px green; width:35%;">
                <select name="searchType" id="searchType" style="width: 20%; height: 35px;">
                   <option value="">선택</option>
                   <option value="fkDepartmentSeq">부서</option>
                   <option value="memberName">이름</option>
                </select>
                <input type="text" name="searchWord" id="searchWord" width="40%" />
                <input type="text" style="display: none;" />
              </li>
              <li style="display: inline-block; width:60%;">
                <select name="gender" id="gender" style="width: 20%; height: 35px;">
                   <option value="">성별선택</option>
                   <option value="0">남</option>
                   <option value="1">여</option>
                </select>
                <button type="button" class="btn btn-danger ml-2" onclick="goSearch()">회원검색</button>
              </li>
            </ul>
         </form>
      </div>
     </div>
   </div>
</body>
