<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="../header/header.jsp" />

<style>
  .attendance-container {
    margin: 120px auto 50px;
    max-width: 1200px;
    font-family: '맑은 고딕', sans-serif;
  }

  /* 상단 전체 레이아웃: 좌 - 출퇴근, 중 - 월 선택, 우 - 요약 */
  .top-section {
    display: flex;
    align-items: center;
    gap: 30px;
    margin-bottom: 30px;
  }

  /* 출퇴근 박스 */
  .work-box {
    border: 1px solid #ccc;
    padding: 20px 25px;
    text-align: center;
    min-width: 180px;
    box-sizing: border-box;
  }
  .work-box h3 {
    margin-bottom: 15px;
    font-size: 16px;
    font-weight: normal;
  }
  .work-time {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 10px;
    display: block;
  }
  .work-label {
    font-weight: normal;
    font-size: 14px;
    margin-bottom: 5px;
  }

  /* 출퇴근 버튼 스타일 */
  .work-btn {
    background-color: #1E90FF;
    color: #fff;
    border: none;
    padding: 6px 12px;
    margin: 5px 8px 0 8px;
    cursor: pointer;
    display: inline-block;
    width: 70px;
    white-space: nowrap;
  }
  .work-btn:hover {
    background-color: #1C86EE;
  }

  /* 월 이동 박스 */
  .month-box {
    border: 1px solid #ccc;
    padding: 10px 15px;
    text-align: center;
    min-width: 110px;
    box-sizing: border-box;
    font-weight: bold;
    user-select: none;
  }
  .month-btn {
    background-color: white;
    border: 1px solid #ccc;
    padding: 5px 12px;
    margin: 0 10px;
    cursor: pointer;
    font-weight: normal;
  }
  .month-btn:hover {
    background-color: #f0f0f0;
  }

  /* 상단 요약 박스 */
  .summary-boxes {
    display: flex;
    gap: 20px;
    margin-bottom: 30px;
  }
  .summary-item {
    flex: 1;
    border: 1px solid #ccc;
    padding: 15px 0;
    text-align: center;
    font-weight: bold;
    font-size: 14px;
    box-sizing: border-box;
  }

  /* 근무 시간 확인 테이블 */
  h3 {
    margin-bottom: 10px;
  }
  table {
    width: 100%;
    border-collapse: collapse;
    background-color: #fefefe;
    box-shadow: 0 0 8px #ddd;
    border-radius: 6px;
    overflow: hidden;
  }
  th, td {
    border-bottom: 1px solid #ddd;
    padding: 10px 12px;
    text-align: center;
    font-size: 14px;
  }
  th {
    background-color: #f5f5f5;
    font-weight: bold;
  }
  tbody tr:last-child td {
    border-bottom: none;
  }
</style>

<div class="attendance-container">

  <!-- 상단: 출퇴근 시간 + 월 선택 + 요약 -->
  <div class="top-section">
    <div class="work-box">
    <h3>
      <c:choose>
        <c:when test="${not empty attendanceList}">
          <fmt:formatDate value="${attendanceList[0].workDate}" pattern="yyyy-MM-dd (E)" />
        </c:when>
        <c:otherwise>
          오늘
        </c:otherwise>
      </c:choose>
    </h3>
    
    <div>
      <span class="work-label">출근시간</span>
      <span class="work-time">
        <c:choose>
          <c:when test="${not empty attendanceList && attendanceList[0].startTime != null}">
            <fmt:formatDate value="${attendanceList[0].startTime}" pattern="HH:mm:ss" />
          </c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </span>
    </div>

    <div>
      <span class="work-label">퇴근시간</span>
      <span class="work-time">
        <c:choose>
          <c:when test="${not empty attendanceList && attendanceList[0].endTime != null}">
            <fmt:formatDate value="${attendanceList[0].endTime}" pattern="HH:mm:ss" />
          </c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </span>
    </div>

    <form action="${ctxPath}/member/startWork" method="get" style="display:inline;">
        <button type="submit" class="work-btn">출근하기</button>
    </form>
    <form action="${ctxPath}/member/endWork" method="get" style="display:inline;">
        <button type="submit" class="work-btn">퇴근하기</button>
    </form>
</div>


    <div class="month-box">
      <button class="month-btn">&lt;-</button>
      <span>1월</span>
      <button class="month-btn">-&gt;</button>
    </div>

    <div class="summary-boxes" style="flex: 1;">
      <div class="summary-item">한달누적근무시간</div>
      <div class="summary-item">근무일수</div>
      <div class="summary-item">사용연차 / 잔여연차</div>
      <div class="summary-item">연장근무</div>
    </div>
  </div>

  <!-- 근무 시간 확인 테이블 -->
  <h3>근무 시간 확인</h3>
  <table>
    <thead>
      <tr>
        <th>근무일</th>
        <th>사원명</th>
        <th>사번</th>
        <th>출근 시각</th>
        <th>퇴근 시각</th>
        <th>근무 시간</th>
      </tr>
    </thead>
    <tbody>
      <tbody>
  <c:forEach var="att" items="${attendanceList}">
    <tr>
      <td><fmt:formatDate value="${att.workDate}" pattern="yyyy-MM-dd" /></td>
      <td>${userName}</td> <!-- 세션 혹은 모델로 사원명 넘겨주세요 -->
      <td>${att.memberSeq}</td>
      <td>
        <c:choose>
          <c:when test="${att.startTime != null}">
            <fmt:formatDate value="${att.startTime}" pattern="HH:mm:ss" />
          </c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </td>
      <td>
        <c:choose>
          <c:when test="${att.endTime != null}">
            <fmt:formatDate value="${att.endTime}" pattern="HH:mm:ss" />
          </c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </td>
      <td>
        <c:choose>
          <c:when test="${att.overtime != null && att.overtime > 0}">
            ${att.overtime}분
          </c:when>
          <c:otherwise>-</c:otherwise>
        </c:choose>
      </td>
    </tr>
  </c:forEach>
</tbody>

  </table>

</div>

<jsp:include page="../footer/footer.jsp" />
