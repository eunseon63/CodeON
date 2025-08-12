<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>  
<%
	String ctxPath = request.getContextPath();
%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>

<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<link rel="stylesheet" type="text/css" href="<%= ctxPath%>/bootstrap-4.6.2-dist/css/bootstrap.min.css">


<script type="text/javascript" src="<%= ctxPath%>/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="<%= ctxPath%>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js" ></script>

<script type="text/javascript">

  $(document).ready(function(){
	  
	  $("button#btnLOGIN").click(function(){
		  func_Login();
	  });
	  
	  
	  $("input#memberPwd").keydown(function(event){
		  
		  if(event.keyCode == 13) { // 엔터를 했을 경우
			  func_Login();
		  }
	  });
	  
  });// end of $(document).ready(function(){})------------------------------
  
  
  // Function Declaration
  function func_Login() {
    const memberUserId = $("input#memberUserId").val(); 
    const memberPwd = $("input#memberPwd").val(); 

    if(memberUserId.trim()=="") {
        alert("아이디를 입력하세요!!");
        $("input#memberUserId").val(""); 
        $("input#memberUserId").focus();
        return;
    }

    if(memberPwd.trim()=="") {
        alert("비밀번호를 입력하세요!!");
        $("input#memberPwd").val(""); 
        $("input#memberPwd").focus();
        return;
    }

    const frm = document.loginFrm;
    frm.action = "<%= ctxPath%>/login/loginEnd";
    frm.method = "POST";
    frm.submit();
}// end of function func_Login()------------------------
</script>
</head>
<body>
<div class="container my-5">
	<div class="row" style="display: flex; border: solid 0px red;">
		<div class="col-md-8 col-md-offset-2" style="margin: auto; border: solid 0px blue;">
			<h2 class="text-muted">로그인</h2>
			<hr style="border: solid 1px orange">
			
			<form name="loginFrm" class="mt-5">
			<div class="form-row">    
			    <div class="form-group col-md-6">
			        <label class="text-muted font-weight-bold" for="memberUserId">아이디</label>
			        <input type="text" class="form-control" name="memberUserId" id="memberUserId" value=""/>
			    </div>
			
			    <div class="form-group col-md-6">
			        <label class="text-muted font-weight-bold text-muted" for="memberPwd">비밀번호</label>
			        <input type="password" class="form-control" name="memberPwd" id="memberPwd" value=""/> 
			    </div>
			</div>
			</form>
		</div>
		<div class="col-md-8 col-md-offset-2" style="margin: auto; display: flex; border: solid 0px blue;">
			<div style="margin: auto; border: solid 0px blue;">
				<button style="width: 150px; height: 40px;" class="btn btn-primary" type="button" id="btnLOGIN">확인</button>
			</div>
		</div>
	</div>
</div>
</body>
</html>