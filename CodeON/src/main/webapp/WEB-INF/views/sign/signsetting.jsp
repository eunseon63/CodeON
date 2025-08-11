<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<jsp:include page="../header/header.jsp" />
<jsp:include page="signsidebar.jsp" />

<style>
html, body {
	height: 100%;
	margin: 0;
	padding: 0;
}

#maininfo {
	margin-left: 220px;
	margin-top: 70px;
	padding: 20px;
	padding-bottom: 80px; /* 여백 추가 */
	min-height: calc(100vh - 70px - 60px);
	box-sizing: border-box;
	text-align: center;
}
</style>

<div id="maininfo">
	<div id="maincontent">
	
	</div>
</div>

<jsp:include page="../footer/footer.jsp" />
