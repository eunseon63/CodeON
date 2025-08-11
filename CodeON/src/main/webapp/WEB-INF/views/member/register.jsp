<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
   String ctxPath = request.getContextPath();
   // ctxPath => 
%>      

<!DOCTYPE html>
<html>
<head>
<!-- Required meta tags -->
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title></title>

<!-- Bootstrap CSS -->
<link rel="stylesheet" href="<%= ctxPath%>/bootstrap-4.6.2-dist/css/bootstrap.min.css" type="text/css">

<%-- Optional JavaScript --%>
<script type="text/javascript" src="<%=ctxPath%>/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="<%=ctxPath%>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js" ></script>

<script type="text/javascript">
$(function() {
    $("#btnRegister").click(function() {
        const formData = $("form[name='sendFrm']").serialize();
        console.log(formData);

        $.ajax({
            url: "<%= ctxPath%>/memberInfo/register",
            type: "POST",
            data: formData,
            dataType: "json",
            success: function(json) {
                console.log(JSON.stringify(json));
                alert("등록이 완료되었습니다.");
                $("form[name='sendFrm']")[0].reset();
            },
            error: function(request, status, error) {
                alert("code: "+request.status+"\nmessage: "+request.responseText+"\nerror: "+error);
            }
        });
    });
});
</script>
</head>

<body>
<div class="form-container">
    <h4 class="form-title">사원 등록</h4>
    <form name="sendFrm">
        <div class="form-row">
            <div class="form-group col-md-6">
                <label>이름</label>
                <input type="text" name="memberName" class="form-control" required>
            </div>
            <div class="form-group col-md-6">
                <label>사원번호</label>
                <input type="text" name="memberSeq" class="form-control" required>
            </div>
        </div>

        <div class="form-row">
            <div class="form-group col-md-6">
                <label>아이디</label>
                <input type="text" name="memberUserid" class="form-control" required>
            </div>
            <div class="form-group col-md-6">
                <label>비밀번호</label>
                <input type="password" name="memberPwd" class="form-control" required>
            </div>
        </div>

        <div class="form-row">
            <div class="form-group col-md-6">
                <label>전화번호</label>
                <input type="text" name="memberPhone" class="form-control" placeholder="010-1234-5678">
            </div>
            <div class="form-group col-md-6">
                <label>이메일</label>
                <input type="email" name="memberEmail" class="form-control">
            </div>
        </div>

        <div class="form-row">
            <div class="form-group col-md-6">
                <label>부서</label>
                <input type="text" name="fkGradeSeq" class="form-control">
            </div>
            <div class="form-group col-md-6">
                <label>직급</label>
                <input type="text" name="fkDepartmentSeq" class="form-control">
            </div>
        </div>

        <div class="form-row">
            <div class="form-group col-md-6">
                <label>주민번호</label>
                <input type="text" name="memberJubun" class="form-control" placeholder="000000-0000000">
            </div>
            <div class="form-group col-md-6">
                <label>입사일자</label>
                <input type="date" name="memberHiredate" class="form-control">
            </div>
        </div>

        <div class="text-center mt-4">
            <button type="button" id="btnRegister" class="btn btn-primary px-4">등록</button>
            <button type="reset" class="btn btn-secondary px-4 ml-2">초기화</button>
        </div>
    </form>
</div>
</body>
</html>