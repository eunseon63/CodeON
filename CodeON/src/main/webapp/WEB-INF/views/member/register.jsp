<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   String ctxPath = request.getContextPath();
%>      

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>직원 등록</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"> 

<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<style>
    /* 전체 레이아웃 설정 */
    body {
        background-color: #f0f2f5; /* 더 부드러운 배경색 */
        padding-top: 70px; /* 헤더 높이만큼 여백 */
        padding-bottom: 60px; /* 푸터 높이만큼 여백 */
    }
    
    .main-container {
        display: flex;
        justify-content: center;
        width: 100%;
        padding-left: 220px; /* 사이드바 너비만큼 여백 */
        box-sizing: border-box;
    }
    
    .register-card {
        margin: 2rem;
        max-width: 1400px; /* 카드의 너비를 목록 페이지와 동일하게 설정 */
        width: 100%;
        border: none; /* 카드 테두리 제거 */
        border-radius: 12px; /* 모서리 둥글게 */
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); /* 그림자 더 부드럽게 */
    }
    
    .card-header {
        background-color: #007bff; /* 헤더 색상을 로고 색상과 맞춤 */
        border-radius: 12px 12px 0 0;
        padding: 1.5rem 1.25rem;
    }
    
    .required-symbol {
        color: #ff6b6b; /* 빨간색을 더 부드러운 색상으로 변경 */
        font-weight: bold;
    }
    
    .form-control, .form-select {
        border-radius: 8px; /* 입력 필드 모서리 둥글게 */
    }
    
    .btn-primary {
        background-color: #007bff;
        border-color: #007bff;
        border-radius: 8px;
        transition: all 0.2s ease;
    }

    .btn-primary:hover {
        background-color: #0056b3;
        border-color: #0056b3;
    }
    
    .btn-outline-secondary {
        border-radius: 8px;
    }

    @media (max-width: 768px) {
        .main-container {
            padding-left: 0;
            flex-direction: column;
            align-items: center;
        }
    }
</style>

<script type="text/javascript">
$(function() {

    function validateForm() {
        const name = $("#memberName").val().trim();
        const userid = $("#memberUserid").val().trim();
        const pwd = $("#memberPwd").val().trim();
        const email = $("#memberEmail").val().trim();
        const mobile = $("#memberMobile").val().trim();
        const department = $("#fkDepartmentSeq").val();
        const grade = $("#fkGradeSeq").val();
        const birthday = $("#memberBirthday").val().trim();
        const hiredate = $("#memberHiredate").val();
        const gender = $("input[name='memberGender']:checked").val();
        
        // 필수 입력 필드 유효성 검사
        if (name === "") { alert("이름을 입력해주세요."); $("#memberName").focus(); return false; }
        if (userid === "") { alert("아이디를 입력해주세요."); $("#memberUserid").focus(); return false; }
        if (pwd === "") { alert("비밀번호를 입력해주세요."); $("#memberPwd").focus(); return false; }
        if (pwd.length < 4 || pwd.length > 12) { alert("비밀번호는 4~12자리로 입력해주세요."); $("#memberPwd").focus(); return false; }
        if (email === "") { alert("이메일을 입력해주세요."); $("#memberEmail").focus(); return false; }
        if (mobile === "") { alert("전화번호를 입력해주세요."); $("#memberMobile").focus(); return false; }
        if (!department) { alert("부서를 선택해주세요."); $("#fkDepartmentSeq").focus(); return false; }
        if (!grade) { alert("직급을 선택해주세요."); $("#fkGradeSeq").focus(); return false; }
        if (birthday === "") { alert("생년월일을 입력해주세요."); $("#memberBirthday").focus(); return false; }
        if (hiredate === "") { alert("입사일자를 입력해주세요."); $("#memberHiredate").focus(); return false; }
        if (!gender) { alert("성별을 선택해주세요."); return false; }

        // 전화번호 형식 검사
        if (mobile !== "" && !/^(010-\d{4}-\d{4}|010\d{8})$/.test(mobile)) {
            alert("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)");
            $("#memberMobile").focus();
            return false;
        }
        
        // 생년월일 형식 검사
        if (birthday !== "" && !/^\d{8}$/.test(birthday)) {
            alert("생년월일은 YYYYMMDD 형식으로 입력하세요.");
            $("#memberBirthday").focus();
            return false;
        }

        // 입사일자 미래 날짜 체크
        if (new Date(hiredate) > new Date()) {
            alert("입사일자는 오늘 이전 날짜여야 합니다.");
            $("#memberHiredate").focus();
            return false;
        }

        return true;
    }

    $("#btnRegister").click(function() {
        if(!validateForm()) return;

        const formData = $("form[name='sendFrm']").serialize();
        console.log(formData);

        $.ajax({
            url: "<%= ctxPath %>/memberInfo/register",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(json) {
                alert("등록이 완료되었습니다.");
                $("form[name='sendFrm']")[0].reset();
            },
            error: function(request, status, error) {
                alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
            }
        });
    });
});
</script>

<body>
<br><br><br>
<div class="container my-5" style="transform: translate(2.5rem, -6.5rem) !important;">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white text-center">
            <h4 class="mb-0">사원 등록</h4>
<div class="main-container">
    <div class="card shadow-sm register-card">
        <div class="card-header text-white text-center d-flex align-items-center justify-content-center py-3">
            <h4 class="mb-0">직원 등록</h4>
        </div>
        <div class="card-body p-4">
            <form name="sendFrm" novalidate>
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="memberName" class="form-label">이름 <span class="required-symbol">*</span></label>
                        <input type="text" name="memberName" id="memberName" class="form-control" required placeholder="홍길동">
                    </div>
                    <div class="col-md-6">
                        <label for="memberUserid" class="form-label">아이디 <span class="required-symbol">*</span></label>
                        <input type="text" name="memberUserid" id="memberUserid" class="form-control" required placeholder="아이디를 입력하세요">
                    </div>
                </div>

                <div class="row g-3 mt-3">
                    <div class="col-md-6">
                        <label for="memberPwd" class="form-label">비밀번호 <span class="required-symbol">*</span></label>
                        <input type="password" name="memberPwd" id="memberPwd" class="form-control" required placeholder="비밀번호를 입력하세요">
                    </div>
                    <div class="col-md-6">
                        <label for="memberEmail" class="form-label">이메일 <span class="required-symbol">*</span></label>
                        <input type="email" name="memberEmail" id="memberEmail" class="form-control" required placeholder="example">
                    </div>
                </div>

                <div class="row g-3 mt-3">
                    <div class="col-12">
                        <label for="memberMobile" class="form-label">전화번호 <span class="required-symbol">*</span></label>
                        <input type="tel" name="memberMobile" id="memberMobile" class="form-control" required placeholder="010-1234-5678">
                    </div>
                </div>

                <div class="row g-3 mt-3">
                    <div class="col-md-6">
                        <label for="fkDepartmentSeq" class="form-label">부서 <span class="required-symbol">*</span></label>
                        <select name="fkDepartmentSeq" id="fkDepartmentSeq" class="form-select" required>
                            <option value="" disabled selected>선택하세요</option>
                            <option value="10">인사팀</option>
                            <option value="20">개발팀</option>
                            <option value="30">기획팀</option>
                            <option value="40">영업팀</option>
                            <option value="50">고객지원팀</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="fkGradeSeq" class="form-label">직급 <span class="required-symbol">*</span></label>
                        <select name="fkGradeSeq" id="fkGradeSeq" class="form-select" required>
                            <option value="" disabled selected>선택하세요</option>
                            <option value="1">사원</option>
                            <option value="2">대리</option>
                            <option value="3">과장</option>
                            <option value="4">부장</option>
                            <option value="5">사장</option>
                        </select>
                    </div>
                </div>

                <div class="row g-3 mt-3 align-items-end">
                    <div class="col-md-4">
                        <label for="memberBirthday" class="form-label">생년월일 <span class="required-symbol">*</span></label>
                        <input type="text" name="memberBirthday" id="memberBirthday" class="form-control" required placeholder="YYYYMMDD">
                    </div>
                    <div class="col-md-4">
                        <label for="memberHiredate" class="form-label">입사일자 <span class="required-symbol">*</span></label>
                        <input type="date" name="memberHiredate" id="memberHiredate" class="form-control" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">성별 <span class="required-symbol">*</span></label>
                        <div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="memberGender" id="genderMale" value="0" required>
                                <label class="form-check-label" for="genderMale">남성</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="memberGender" id="genderFemale" value="1" required>
                                <label class="form-check-label" for="genderFemale">여성</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="d-flex justify-content-center mt-4">
                    <button type="button" id="btnRegister" class="btn btn-primary px-5 me-2">
                         <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-person-plus me-1" viewBox="0 0 16 16">
                            <path d="M6 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm4 4s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H8zm-1-9a.5.5 0 0 1 .5-.5h2a.5.5 0 0 1 0 1H9.5V7a.5.5 0 0 1-1 0V4.5H7a.5.5 0 0 1 0-1z"/>
                        </svg>
                        등록
                    </button>
                    <button type="reset" class="btn btn-outline-secondary px-5">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-counterclockwise me-1" viewBox="0 0 16 16">
                            <path fill-rule="evenodd" d="M8 3a5 5 0 1 1-4.57 7.5l-.5-.87A4 4 0 1 0 8 4a4 4 0 0 0-4 4v.5a.5.5 0 0 1-1 0V8a5 5 0 0 1 10 0v1.5a.5.5 0 0 1-.5.5z"/>
                        </svg>
                        초기화
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>

<jsp:include page="../footer/footer.jsp" />
</html>