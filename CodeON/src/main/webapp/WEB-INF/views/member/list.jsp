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
    allMember();
});

// 전체 회원 조회
function allMember() {
    $.ajax({
        url: "<%= ctxPath%>/memberInfo/allMember",
        dataType: "json",
        success: function(json) {
            renderTable(json);
        },
        error: function(request, status, error){
            alert("code: "+request.status+"\nmessage: "+request.responseText+"\nerror: "+error);
        } 
    });
}

// 테이블 렌더링
function renderTable(json) {
    let v_html = "";
    if (json.length === 0) {
        v_html = `<tr><td colspan='7'>가입된 회원이 없습니다.</td></tr>`;
    } else {
        $.each(json, function(index, item) {
            v_html += `
                <tr>
                    <td>\${item.memberHiredate}</td>
                    <td>\${item.memberSeq}</td>
                    <td>\${
                        item.fkDepartmentSeq === 10 ? '인사팀' :
                        item.fkDepartmentSeq === 20 ? '개발팀' :
                        item.fkDepartmentSeq === 30 ? '기획팀' :
                        item.fkDepartmentSeq === 40 ? '영업팀' :
                        item.fkDepartmentSeq === 50 ? '고객지원팀' : ''
                    }</td>
                    <td>\${
                        item.fkGradeSeq == 1 ? '사원' :
                        item.fkGradeSeq == 2 ? '대리' :
                        item.fkGradeSeq == 3 ? '과장' :
                        item.fkGradeSeq == 4 ? '부장' :
                        item.fkGradeSeq == 5 ? '사장' : ''
                    	}
                    </td>
                    <td>\${item.memberName}</td>
                    <td>\${item.memberEmail}</td>
                    <td>
                        <button class='btn btn-sm btn-success mr-2' onclick='goUpdate("${item.memberSeq}")'>수정</button>
                        <button class='btn btn-sm btn-danger' onclick='goDelete("${item.memberSeq}")'>삭제</button>
                    </td>
                </tr>
            `;
        });
    }
    $('#memberTableBody').html(v_html);
}

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

// 회원 검색
function goSearch() {
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
            renderTable(json);
        },
        error: function(request, status, error) {
            alert("code: "+request.status+"\nmessage: "+request.responseText+"\nerror: "+error);
        }
    });
}
</script>

<br><br>

<body class="bg-light">
<div class="container py-5">
    <div class="card search-card mb-4">
        <div class="card-body">
            <h5 class="card-title mb-3 text-primary font-weight-bold">사원 검색</h5>
            <form class="form-inline">
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
                        <!-- AJAX로 데이터 삽입 -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>

<jsp:include page="../footer/footer.jsp" />
