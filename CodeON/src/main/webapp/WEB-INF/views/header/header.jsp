<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- header.css 따로 분리 권장 -->
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
    z-index: 1000;
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
    text-decoration: none;
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
    transition: all 0.2s ease;
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
    transition: background-color 0.2s ease;
  }
  .logout-btn:hover {
    background-color: #1C86EE;
  }
</style>

<header>
  <div class="left-section">
    <!-- 로고 전체 클릭 가능 -->
    <a href="<%= ctxPath %>/index" class="logo">
      <img src="<%= ctxPath %>/image/logo.png" alt="CODEON 로고" />
    </a>

    <nav>
      <a href="#">게시판</a>
      <a href="#">메일</a>
      <a href="<%= ctxPath %>/schedule/list">일정</a>
      <a href="<%= ctxPath %>/member/work">근태관리</a>
      <a href="<%= ctxPath %>/sign/main">전자결재</a>
      <a href="<%= ctxPath %>/address">주소록</a>
      <a href="<%= ctxPath %>/mypage">마이페이지</a>
      <a href="<%= ctxPath %>/member/register">인사(인사팀만)</a>
    </nav>
  </div>

  <form action="<%= ctxPath %>/login/logout" method="get">
    <button type="submit" class="logout-btn">로그아웃</button>
  </form>
</header>
