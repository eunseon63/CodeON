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
    $("#btnRegister").click(function() {
        const formData = $("form[name='sendFrm']").serialize();
        console.log(formData);

        $.ajax({
            url: "<%= ctxPath %>/memberInfo/register",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(json) {
                // console.log(JSON.stringify(json));
                alert("등록이 완료되었습니다.");
                $("form[name='sendFrm']")[0].reset(); // 등록 후 폼 초기화
            },
            error: function(request, status, error) {
                alert("code: " + request.status + "\nmessage: " + request.responseText + "\nerror: " + error);
            }
        });
    });
}); // end of $(function() {})----------------
</script>

<body>
<br><br>
<div class="container my-5" style="margin-left: 220px;">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white text-center">
            <h4 class="mb-0">사원 등록</h4>
        </div>
        <div class="card-body">
            <form name="sendFrm" novalidate>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="memberName">이름 <small class="text-danger">*</small></label>
                        <input type="text" name="memberName" id="memberName" class="form-control" required placeholder="홍길동">
                    </div>
                    <div class="form-group col-md-6">
                        <label for="memberUserid">아이디 <small class="text-danger">*</small></label>
                        <input type="text" name="memberUserid" id="memberUserid" class="form-control" required placeholder="영문, 숫자 조합">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="memberPwd">비밀번호 <small class="text-danger">*</small></label>
                        <input type="password" name="memberPwd" id="memberPwd" class="form-control" required placeholder="비밀번호를 입력하세요">
                    </div>
                    <div class="form-group col-md-6">
                        <label for="memberEmail">이메일 <small class="text-danger">*</small></label>
                        <input type="email" name="memberEmail" id="memberEmail" class="form-control" required placeholder="example@domain.com">
                    </div>
                </div>

                <div class="form-group">
                    <label for="memberMobile">전화번호</label>
                    <input type="tel" name="memberMobile" id="memberMobile" class="form-control" placeholder="010-1234-5678">
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="fkDepartmentSeq">부서 <small class="text-danger">*</small></label>
                        <select name="fkDepartmentSeq" id="fkDepartmentSeq" class="form-control" required>
                            <option value="" disabled selected>선택하세요</option>
                            <option value="10">인사팀</option>
                            <option value="20">개발팀</option>
                            <option value="30">기획팀</option>
                        </select>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fkGradeSeq">직급 <small class="text-danger">*</small></label>
                        <select name="fkGradeSeq" id="fkGradeSeq" class="form-control" required>
                            <option value="" disabled selected>선택하세요</option>
                            <option value="1">사원</option>
                            <option value="2">대리</option>
                            <option value="3">과장</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-4">
                        <label for="memberBirthday">생년월일</label>
                        <input type="text" name="memberBirthday" id="memberBirthday" class="form-control" placeholder="YYYYMMDD">
                    </div>
                    <div class="form-group col-md-4">
                        <label for="memberHiredate">입사일자</label>
                        <input type="date" name="memberHiredate" id="memberHiredate" class="form-control">
                    </div>
                    <div class="form-group col-md-4">
                        <label class="d-block">성별</label>
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

                <div class="text-center mt-4">
                    <button type="button" id="btnRegister" class="btn btn-primary px-5 me-2">등록</button>
                    <button type="reset" class="btn btn-outline-secondary px-5">초기화</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>

<jsp:include page="../footer/footer.jsp" />
