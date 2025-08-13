<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>

<style>
  header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 25px;
    border-bottom: 1px solid #ccc;
    background-color: white;
    height: 70px;
    box-sizing: border-box;
    position: fixed;
    width: 100%;
   	top: 0;
  }
  .left-section {
    display: flex;
    align-items: center;
    gap: 25px;
  }
  .logo {
    display: flex;
    align-items: center;
    font-weight: 700;
    font-size: 26px;
    color: #0055a5;
  }
  .logo img {
    height: 55px; 
    margin-right: 10px; 
  }
  nav {
    display: flex;
    gap: 20px; 
    font-weight: 600;
    font-size: 17px;
  }
  nav a {
    text-decoration: none;
    color: black;
    padding-bottom: 2px;
    border-bottom: 2px solid transparent;
  }
  nav a:hover {
    border-bottom: 2px solid black;
    font-weight: 700;
  }
  .logout-btn {
    background-color: #1E90FF;
    color: white;
    padding: 6px 14px;
    border: none;
    border-radius: 3px;
    font-size: 15px;
    cursor: pointer;
  }
  .logout-btn:hover {
    background-color: #1C86EE;
  }
</style>
  <%-- Optional JavaScript --%>
  <script type="text/javascript" src="<%=ctxPath%>/js/jquery-3.7.1.min.js"></script>
  <script type="text/javascript" src="<%=ctxPath%>/bootstrap-4.6.2-dist/js/bootstrap.bundle.min.js" ></script>
  <script type="text/javascript" src="<%=ctxPath%>/smarteditor/js/HuskyEZCreator.js" charset="utf-8"></script> 
  
<%-- 스피너 및 datepicker 를 사용하기 위해 jQueryUI CSS 및 JS --%>
    <link rel="stylesheet" type="text/css" href="<%=ctxPath%>/jquery-ui-1.13.1.custom/jquery-ui.min.css" />
    <script type="text/javascript" src="<%=ctxPath%>/jquery-ui-1.13.1.custom/jquery-ui.min.js"></script>

<header>
  <div class="left-section">
    <div class="logo">
      <img src="<%= ctxPath %>/image/logo.png" alt="CODEON 로고" />
    </div>
    <nav>
      <a href="<%= ctxPath %>/board/list">게시판</a>
      <a href="#">메일</a>
      <a href="#">일정</a>
      <a href="#">근태관리</a>
      <a href="<%= ctxPath %>/sign/main">전자결재</a>
      <a href="#">주소록</a>
      <a href="#">마이페이지</a>
      <a href="#">인사(인사팀만)</a>
    </nav>
  </div>

  <form action="<c:url value='/logout' />" method="post">
    <button type="submit" class="logout-btn">로그아웃</button>
  </form>
</header>
