<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
   String ctxPath = request.getContextPath();
%>      
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<link rel="stylesheet" href="<%= ctxPath %>/bootstrap-4.6.2-dist/css/bootstrap.min.css" type="text/css"> 

<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />

<meta charset="UTF-8">
<title>사원 목록</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

<style>
    .search-card {
        border-radius: 0.75rem;
        box-shadow: 0 0.125rem 0.25rem rgba(0,0,0,.075);
    }
    table th, table td {
        vertical-align: middle !important;
        text-align: center;
    }
</style>

<script type="text/javascript">
// 페이지 로드 시 전체 회원 정보 출력
$(function() {
	
	$("input#searchWord").keyup(function(e){
		if(e.keyCode == 13) {
			// 검색어에 엔터를 했을 경우
			goSearch();
		}
	});
	
	// 검색시 검색조건 및 검색어 값 유지시키기
	if( ${requestScope.searchType != "" && requestScope.searchWord != "" && requestScope.gender != ""} ) {
		$("select#searchType").val("${requestScope.searchType}");
		$("input#searchWord").val("${requestScope.searchWord}");
		$("select#gender").val("${requestScope.gender}");
	}
		
});

// 전체 회원 조회
function goSearch() {
	const frm = document.searchFrm;
	frm.method = "GET";
	frm.action = "<%= ctxPath%>/member/list";
	frm.submit();
}// end of function goSearch()--------------------

// 회원 삭제
function goDelete(memberSeq) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        $.ajax({
            url: "<%= ctxPath%>/memberInfo/delete",
            type: "delete",
            data: { "memberSeq": memberSeq },
            dataType: "json",
            success: function(json) {
                if (json.n == 1) {
                    alert("삭제가 완료되었습니다.");
                    allMember();
                }
            },
            error: function(request, status, error){
                alert("code: "+request.status+"\nmessage: "+request.responseText+"\nerror: "+error);
            } 
        });
    }
}

//회원 수정
function goUpdate(memberSeq) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        $.ajax({
            url: "<%= ctxPath%>/memberInfo/update",
            type: "delete",
            data: { "memberSeq": memberSeq },
            dataType: "json",
            success: function(json) {
                if (json.n == 1) {
                    alert("삭제가 완료되었습니다.");
                    allMember();
                }
            },
            error: function(request, status, error){
                alert("code: "+request.status+"\nmessage: "+request.responseText+"\nerror: "+error);
            } 
        });
    }
}
</script>

<br><br>

<body class="bg-light">
<div class="container py-5">
    <div class="card search-card mb-4">
        <div class="card-body">
            <h5 class="card-title mb-3 text-primary font-weight-bold">사원 검색</h5>
            <form class="form-inline" name="searchFrm">
                <div class="form-group mr-2">
                    <select class="form-control" id="searchType" name="searchType">
                        <option value="">검색 기준</option>
                        <option value="fkDepartmentSeq">부서</option>
                        <option value="fkGradeSeq">직급</option>
                        <option value="memberName">이름</option>
                    </select>
                </div>
                <div class="form-group mr-2">
                    <input type="text" class="form-control" id="searchWord" name="searchWord" placeholder="검색어 입력">
                </div>
                <div class="form-group mr-2">
                    <select class="form-control" id="gender" name="gender">
                        <option value="">성별</option>
                        <option value="0">남</option>
                        <option value="1">여</option>
                    </select>
                </div>
                <button type="button" class="btn btn-danger" onclick="goSearch()">검색</button>
            </form>
        </div>
    </div>

    <div class="card search-card">
        <div class="card-body">
            <h5 class="card-title mb-3 text-primary font-weight-bold">사원 목록</h5>
            <div class="table-responsive">
                <table class="table table-hover table-bordered">
                    <thead class="thead-light">
                        <tr>
                            <th>입사일</th>
                            <th>사원번호</th>
                            <th>부서</th>
                            <th>직급</th>
                            <th>이름</th>
                            <th>이메일</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody id="memberTableBody">
					    <c:if test="${empty MemberDtoList}">
					        <tr>
					            <td colspan="7">가입된 회원이 없습니다.</td>
					        </tr>
					    </c:if>
					
					    <c:if test="${not empty MemberDtoList}">
					        <c:forEach var="item" items="${MemberDtoList}">
					            <tr class="exists">
					                <td>${item.memberHiredate}</td>
					                <td>${item.memberSeq}</td>
					                <td>
					                    <c:choose>
					                        <c:when test="${item.fkDepartmentSeq == 10}">인사팀</c:when>
					                        <c:when test="${item.fkDepartmentSeq == 20}">개발팀</c:when>
					                        <c:when test="${item.fkDepartmentSeq == 30}">기획팀</c:when>
					                        <c:when test="${item.fkDepartmentSeq == 40}">영업팀</c:when>
					                        <c:when test="${item.fkDepartmentSeq == 50}">고객지원팀</c:when>
					                    </c:choose>
					                </td>
					                <td>
					                    <c:choose>
					                        <c:when test="${item.fkGradeSeq == 1}">사원</c:when>
					                        <c:when test="${item.fkGradeSeq == 2}">대리</c:when>
					                        <c:when test="${item.fkGradeSeq == 3}">과장</c:when>
					                        <c:when test="${item.fkGradeSeq == 4}">부장</c:when>
					                        <c:when test="${item.fkGradeSeq == 5}">사장</c:when>
					                    </c:choose>
					                </td>
					                <td>${item.memberName}</td>
					                <td>${item.memberEmail}</td>
					                <td>
					                    <button class="btn btn-sm btn-success mr-2" onclick="goUpdate('${item.memberSeq}')">수정</button>
					                    <button class="btn btn-sm btn-danger" onclick="goDelete('${item.memberSeq}')">삭제</button>
					                </td>
					            </tr>
					        </c:forEach>
					    </c:if>
                    </tbody>
                </table>
                
              	<div align="center" style="border: solid 0px gray; width: 80%; margin: 30px auto;">  
		    		${requestScope.pageBar}
	   			</div>
            </div>
        </div>
    </div>
</div>
</body>

<jsp:include page="../footer/footer.jsp" />
