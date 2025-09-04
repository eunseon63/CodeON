<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String ctxPath = request.getContextPath();
%>

<html>
<head>
<jsp:include page="/WEB-INF/views/header/header.jsp" />

<meta charset="UTF-8">
<title>일정 등록</title>

<style>
:root{ --bg:#f4f6f8; --card:#ffffff; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --brand:#22c55e; --shadow:0 8px 20px rgba(0,0,0,.08); }
*{box-sizing:border-box}
body{margin:0; background:var(--bg); color:var(--text); font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans KR",Arial,Helvetica,sans-serif;}
.container{max-width:760px; margin:46px auto; background:var(--card); border:1px solid var(--line); border-radius:16px; box-shadow:var(--shadow); padding:28px 34px;}
h2{margin:0 0 18px; text-align:center}
label{font-weight:600; color:#374151; margin-top:14px; display:block}
input, textarea, select{width:100%; padding:10px 12px; margin-top:6px; border:1px solid #cfd4dc; border-radius:10px; font-size:14px; background:#fafafa; transition:all .2s}
input:focus, textarea:focus, select:focus{outline:none; border-color:#8b5cf6; background:#fff; box-shadow:0 0 0 3px rgba(139,92,246,.15)}
textarea{resize:vertical; min-height:110px}
.row{display:grid; grid-template-columns:1fr 1fr; gap:14px}
.helper{font-size:12px; color:var(--muted); margin-top:6px}
.btn-submit{margin-top:22px; width:100%; padding:12px; background:linear-gradient(90deg,#22c55e,#16a34a); border:none; border-radius:10px; color:#fff; font-size:16px; font-weight:700; cursor:pointer}
.tag{display:inline-block; padding:4px 10px; border-radius:999px; background:#ecfdf5; color:#065f46; font-size:12px; margin-left:8px}
.hidden{display:none}
</style>
</head>

<body>
<div class="container">

  <%-- 1) 목록에서 탭을 누르고 왔으면 kind 파라미터가 있음. 없으면 기본 PERSONAL(개인). --%>
  <c:set var="rawKind" value="${param.kind}" />
  <c:choose>
    <c:when test="${rawKind == 'COMPANY' || rawKind == '사내'}"><c:set var="kind" value="COMPANY"/></c:when>
    <c:when test="${rawKind == 'DEPARTMENT' || rawKind == '부서'}"><c:set var="kind" value="DEPARTMENT"/></c:when>
    <c:when test="${rawKind == 'SHARE' || rawKind == '공유'}"><c:set var="kind" value="SHARE"/></c:when>
    <c:otherwise><c:set var="kind" value="PERSONAL"/></c:otherwise>
  </c:choose>

  <%-- 2) bigCategoryList에서 해당 kind의 대분류 seq 찾기 (특히 PERSONAL=개인) --%>
  <c:set var="companySeq"  value=""/>
  <c:set var="deptSeq"     value=""/>
  <c:set var="personalSeq" value=""/>
  <c:set var="shareSeq"    value=""/>
  <c:forEach var="bigCat" items="${bigCategoryList}">
    <c:if test="${fn:contains(bigCat.bigCategoryName,'사내')}"><c:set var="companySeq"  value="${bigCat.bigCategorySeq}"/></c:if>
    <c:if test="${fn:contains(bigCat.bigCategoryName,'부서')}"><c:set var="deptSeq"     value="${bigCat.bigCategorySeq}"/></c:if>
    <c:if test="${fn:contains(bigCat.bigCategoryName,'개인')}"><c:set var="personalSeq" value="${bigCat.bigCategorySeq}"/></c:if>
    <c:if test="${fn:contains(bigCat.bigCategoryName,'공유')}"><c:set var="shareSeq"    value="${bigCat.bigCategorySeq}"/></c:if>
  </c:forEach>

  <c:set var="selectedBigSeq"
         value="${kind=='COMPANY' ? companySeq :
                 kind=='DEPARTMENT' ? deptSeq :
                 kind=='SHARE' ? shareSeq : personalSeq}"/>

  <h2>
    일정 등록
    <span class="tag">
      <c:choose>
        <c:when test="${kind=='COMPANY'}">사내</c:when>
        <c:when test="${kind=='DEPARTMENT'}">부서</c:when>
        <c:when test="${kind=='SHARE'}">공유</c:when>
        <c:otherwise>내 캘린더(개인)</c:otherwise>
      </c:choose>
    </span>
  </h2>

  <%-- 3) 폼: 캘린더/대분류 선택 UI 제거. bigCategorySeq를 hidden으로 전송(중복 제거). --%>
  <form action="<%= ctxPath %>/Calendar/addCalendarForm" method="post" onsubmit="return validateForm();">
    <input type="hidden" name="bigCategorySeq" value="${selectedBigSeq}"/>
    <input type="hidden" name="calendarType"  value="${kind}"/><!-- 필요 시 백엔드 로깅/분기용 -->

    <%-- (선택) 소분류: 선택된 대분류에 속한 것만 보여줌 --%>
    <label>소분류 선택</label>
    <select name="smallCategorySeq" id="smallCategorySeq">
      <option value="">-- 선택하세요 --</option>
      <c:forEach var="smallCat" items="${smallCategoryList}">
        <c:if test="${smallCat.fkBigCategorySeq == selectedBigSeq}">
          <option value="${smallCat.smallCategorySeq}" data-fk="${smallCat.fkBigCategorySeq}">
            ${smallCat.smallCategoryName}
          </option>
        </c:if>
      </c:forEach>
    </select>

    <%-- 제목/내용 --%>
    <label>일정 제목</label>
    <input type="text" id="title" name="title" placeholder="일정 제목을 입력하세요" required>

    <label>일정 내용</label>
    <textarea name="content" placeholder="세부 내용을 입력하세요"></textarea>

    <%-- 시작/종료 --%>
    <div class="row">
      <div>
        <label>시작 날짜</label>
        <input type="datetime-local" id="startDate" name="startDate" value="${param.date}T09:00" required>
      </div>
      <div>
        <label>종료 날짜</label>
        <input type="datetime-local" id="endDate" name="endDate" value="${param.date}T10:00" required>
      </div>
    </div>

    <%-- 장소/색상(선택) --%>
    <div class="row">
      <div>
        <label>장소</label>
        <input type="text" id="calendarLocation" name="calendarLocation" placeholder="회의실 A, 온라인 등">
      </div>
      <div>
        <label>색상</label>
        <input type="color" id="calendarColor" name="calendarColor" value="">
        <div class="helper">미선택 시 서버/DB 기본 정책을 따릅니다.</div>
      </div>
    </div>

    <%-- 공유 탭에서 진입했을 때만 공유자 입력 --%>
    <c:if test="${kind=='SHARE'}">
      <div id="shareEmployeesGroup">
        <label>공유할 직원</label>
        <input type="text" id="shareEmployees" name="shareEmployees" placeholder="사원번호 또는 사원이름, 쉼표(,)로 구분">
        <div class="helper">예: 101, 205, 김철수</div>
      </div>
    </c:if>

    <%-- 반복 --%>
    <label>반복</label>
    <select name="repeatType">
      <option value="NONE">반복 없음</option>
      <option value="DAILY">매일</option>
      <option value="WEEKLY">매주</option>
      <option value="MONTHLY">매월</option>
    </select>

    <button type="submit" class="btn-submit">등록하기</button>
  </form>
</div>

<script>
function validateForm(){
  const title = document.getElementById('title').value.trim();
  const start = document.getElementById('startDate').value;
  const end   = document.getElementById('endDate').value;
  if(!title){ alert('제목을 입력하세요.'); return false; }
  if(!start || !end){ alert('시작/종료 일시를 모두 입력하세요.'); return false; }
  if(new Date(start) > new Date(end)){ alert('종료일은 시작일 이후여야 합니다.'); return false; }

  // 공유일 때만 공유자 필수
  var isShare = '<c:out value="${kind}"/>' === 'SHARE';
  if(isShare){
    const share = (document.getElementById('shareEmployees')?.value || '').trim();
    if(!share){ alert('공유 캘린더는 공유할 직원을 입력하세요.'); return false; }
  }

  // 개인(내 캘린더) 고정 보장 체크
  const big = '<c:out value="${selectedBigSeq}"/>';
  if(!big){ alert('개인 대분류가 설정되지 않았습니다. 관리자에게 문의해주세요.'); return false; }

  return true;
}
</script>

<jsp:include page="../footer/footer.jsp" />
</body>
</html>
