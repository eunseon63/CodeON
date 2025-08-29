
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.net.InetAddress" %>
<%
    String ctxPath = request.getContextPath();
%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    // === (#웹채팅관련2) ===
    // === 서버 IP 주소 알아오기(사용중인 IP주소가 유동IP 이라면 IP주소를 알아와야 한다.) === 
    
    InetAddress inet = InetAddress.getLocalHost();
    String serverIP = inet.getHostAddress();
   
 // System.out.println("serverIP : " + serverIP);
 // serverIP : 192.168.0.14
 
 // String serverIP = "192.168.0.14";
    // 자신의 EC2 퍼블릭 IPv4 주소임. // 아마존(AWS)에 배포를 하기 위한 것임. 
    // 만약에 사용중인 IP주소가 고정IP 이라면 IP주소를 직접입력해주면 된다. 

    // === 서버 포트번호 알아오기 === //
    int portnumber = request.getServerPort();
 // System.out.println("portnumber : " + portnumber);
 // portnumber : 9090
 
    String serverName = "http://"+serverIP+":"+portnumber;
 // System.out.println("serverName : " + serverName);
 // serverName : http://192.168.0.14:9090 
    
%>

<style>
  :root {
    --header-height: 70px;
  }
  /* 전체 기본 여백 초기화 */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  body {
    font-family: '맑은 고딕', sans-serif;
    padding-top: var(--header-height); /* 헤더 높이만큼 아래로 */
    background-color: #f8f9fb;
  }
  .site-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 25px;
    border-bottom: 1px solid #ccc;
    background-color: white;
    height: var(--header-height);
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
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

  /* 본문 공통 */
  main {
    padding: 24px;
    min-height: calc(100vh - var(--header-height));
  }
</style>

<header class="site-header">
  <div class="left-section">
    <a href="<%= ctxPath %>/index" class="logo">
      <img src="<%= ctxPath %>/image/logo.png" alt="CODEON 로고" />
    </a>

    <nav>
      <a href="<%= ctxPath %>/board/list">게시판</a>
      <a href="<%= ctxPath %>/mail/list">메일</a>
      <a href="<%= ctxPath %>/schedule/scheduleManagement">일정</a>
      <a href="<%= ctxPath %>/member/work">근태관리</a>
      <a href="<%= ctxPath %>/sign/main">전자결재</a>
      <a href="<%= ctxPath %>/address">주소록</a>
      <a href="<%= ctxPath %>/mypage">마이페이지</a>
      <a href="<%= ctxPath %>/chatting/multichat">웹채팅</a>
      <a href="<%= ctxPath %>/member/register">인사(인사팀만)</a>
      <a href="<%= ctxPath %>/company/organization">사내 조직도</a>
      
    </nav>
  </div>

  <form action="<%= ctxPath %>/login/logout" method="get">
    <button type="submit" class="logout-btn">로그아웃</button>
  </form>
  	
</header>

