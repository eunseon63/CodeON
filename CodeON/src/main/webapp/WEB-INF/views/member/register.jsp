<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
   String ctxPath = request.getContextPath();
%>      
<link rel="stylesheet" href="<%= ctxPath %>/bootstrap-4.6.2-dist/css/bootstrap.min.css" type="text/css"> 
<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>사원 등록</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

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

        // 이름
        if(name === "") {
            alert("이름을 입력해주세요.");
            $("#memberName").focus();
            return false;
        }

        // 아이디
        if(userid === "") {
            alert("아이디를 입력해주세요.");
            $("#memberUserid").focus();
            return false;
        }

        // 비밀번호
        if(pwd === "") {
            alert("비밀번호를 입력해주세요.");
            $("#memberPwd").focus();
            return false;
        } else if(pwd.length < 4 || pwd.length > 12) {
            alert("비밀번호는 4~12자리로 입력해주세요.");
            $("#memberPwd").focus();
            return false;
        }

        // 이메일
        if(email === "") {
            alert("이메일을 입력해주세요.");
            $("#memberEmail").focus();
            return false;
        }

        // 전화번호 (선택 입력, 입력 시 형식 체크)
        if(mobile !== "" && !/^\d{3}-\d{3,4}-\d{4}$/.test(mobile)) {
            alert("전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)");
            $("#memberMobile").focus();
            return false;
        }

        // 부서
        if(!department) {
            alert("부서를 선택해주세요.");
            $("#fkDepartmentSeq").focus();
            return false;
        }

        // 직급
        if(!grade) {
            alert("직급을 선택해주세요.");
            $("#fkGradeSeq").focus();
            return false;
        }

        // 생년월일 (선택 입력, 입력 시 형식 체크)
        if(birthday !== "" && !/^(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$/.test(birthday)) {
            alert("생년월일 형식이 올바르지 않습니다. (예: 19900101)");
            $("#memberBirthday").focus();
            return false;
        }

        // 입사일자 (선택 입력, 미래 날짜 체크)
        if(hiredate !== "" && new Date(hiredate) > new Date()) {
            alert("입사일자는 오늘 이전 날짜여야 합니다.");
            $("#memberHiredate").focus();
            return false;
        }

        // 성별
        if(!gender) {
            alert("성별을 선택해주세요.");
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
});  // end of $(function() {})----------------
</script>

<body>
<br><br><br>
<div class="container my-5">
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
                    <label for="memberMobile">전화번호<small class="text-danger">*</small></label>
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
                            <option value="40">영업팀</option>
                            <option value="50">고객지원팀</option>
                        </select>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fkGradeSeq">직급 <small class="text-danger">*</small></label>
                        <select name="fkGradeSeq" id="fkGradeSeq" class="form-control" required>
                            <option value="" disabled selected>선택하세요</option>
                            <option value="1">사원</option>
                            <option value="2">대리</option>
                            <option value="3">과장</option>
                            <option value="4">부장</option>
                            <option value="5">사장</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-4">
                        <label for="memberBirthday">생년월일<small class="text-danger">*</small></label>
                        <input type="text" name="memberBirthday" id="memberBirthday" class="form-control" placeholder="YYYYMMDD">
                    </div>
                    <div class="form-group col-md-4">
                        <label for="memberHiredate">입사일자<small class="text-danger">*</small></label>
                        <input type="date" name="memberHiredate" id="memberHiredate" class="form-control">
                    </div>
                    <div class="form-group col-md-4">
                        <label class="d-block">성별<small class="text-danger">*</small></label>
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
