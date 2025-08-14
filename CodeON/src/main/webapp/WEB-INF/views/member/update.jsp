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
<title>사원 수정</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

<script type="text/javascript">
$(function() {
    $("#btnRegister").click(function() {
        const formData = $("form[name='updateFrm']").serialize();
        console.log(formData);

        $.ajax({
            url: "<%= ctxPath %>/memberInfo/update",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(json) {
                alert("수정이 완료되었습니다.");
                $("form[name='updateFrm']")[0].reset();
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
<div class="container my-5">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white text-center">
            <h4 class="mb-0">사원 수정</h4>
        </div>
        <div class="card-body">
            <form name="updateFrm" novalidate>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="memberName">이름 <small class="text-danger">*</small></label>
                        <input type="text" name="memberName" id="memberName" class="form-control"
                               value="${requestScope.mbrDto.memberName}" readonly>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="memberUserid">아이디 <small class="text-danger">*</small></label>
                        <input type="text" name="memberUserid" id="memberUserid" class="form-control"
                               value="${requestScope.mbrDto.memberUserid}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="memberPwd">비밀번호 <small class="text-danger">*</small></label>
                        <input type="password" name="memberPwd" id="memberPwd" class="form-control"
                               value="${requestScope.mbrDto.memberPwd}">
                    </div>
                    <div class="form-group col-md-6">
                        <label for="memberEmail">이메일 <small class="text-danger">*</small></label>
                        <input type="email" name="memberEmail" id="memberEmail" class="form-control"
                               value="${requestScope.mbrDto.memberEmail}">
                    </div>
                </div>

                <div class="form-group">
                    <label for="memberMobile">전화번호</label>
                    <input type="tel" name="memberMobile" id="memberMobile" class="form-control"
                           value="${requestScope.mbrDto.memberMobile}">
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="fkDepartmentSeq">부서 <small class="text-danger">*</small></label>
                        <select name="fkDepartmentSeq" id="fkDepartmentSeq" class="form-control" required>
                            <option value="" disabled>선택하세요</option>
                            <option value="10" ${requestScope.mbrDto.fkDepartmentSeq == 10 ? "selected" : ""}>인사팀</option>
                            <option value="20" ${requestScope.mbrDto.fkDepartmentSeq == 20 ? "selected" : ""}>개발팀</option>
                            <option value="30" ${requestScope.mbrDto.fkDepartmentSeq == 30 ? "selected" : ""}>기획팀</option>
                            <option value="40" ${requestScope.mbrDto.fkDepartmentSeq == 40 ? "selected" : ""}>영업팀</option>
                            <option value="50" ${requestScope.mbrDto.fkDepartmentSeq == 50 ? "selected" : ""}>고객지원팀</option>
                        </select>
                    </div>
                    <div class="form-group col-md-6">
                        <label for="fkGradeSeq">직급 <small class="text-danger">*</small></label>
                        <select name="fkGradeSeq" id="fkGradeSeq" class="form-control" required>
                            <option value="">선택하세요</option>
                            <option value="1" ${requestScope.mbrDto.fkGradeSeq == 1 ? "selected" : ""}>사원</option>
                            <option value="2" ${requestScope.mbrDto.fkGradeSeq == 2 ? "selected" : ""}>대리</option>
                            <option value="3" ${requestScope.mbrDto.fkGradeSeq == 3 ? "selected" : ""}>과장</option>
                            <option value="4" ${requestScope.mbrDto.fkGradeSeq == 4 ? "selected" : ""}>부장</option>
                            <option value="5" ${requestScope.mbrDto.fkGradeSeq == 5 ? "selected" : ""}>사장</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-4">
                        <label for="memberBirthday">생년월일</label>
                        <input type="text" name="memberBirthday" id="memberBirthday" class="form-control" placeholder="YYYYMMDD"
                               value="${requestScope.mbrDto.memberBirthday}">
                    </div>
                    <div class="form-group col-md-4">
                        <label for="memberHiredate">입사일자</label>
                        <input type="date" name="memberHiredate" id="memberHiredate" class="form-control"
                               value="${requestScope.mbrDto.memberHiredate}">
                    </div>
                    <div class="form-group col-md-4">
                        <label class="d-block">성별</label>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="memberGender" id="genderMale" value="0"
                                   ${requestScope.mbrDto.memberGender == 0 ? "checked" : ""} required>
                            <label class="form-check-label" for="genderMale">남성</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="memberGender" id="genderFemale" value="1"
                                   ${requestScope.mbrDto.memberGender == 1 ? "checked" : ""} required>
                            <label class="form-check-label" for="genderFemale">여성</label>
                        </div>
                    </div>
                </div>

                <div class="text-center mt-4">
                    <button type="button" id="btnRegister" class="btn btn-primary px-5 me-2">수정</button>
                    <button type="reset" class="btn btn-outline-secondary px-5">초기화</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>

<jsp:include page="../footer/footer.jsp" />
