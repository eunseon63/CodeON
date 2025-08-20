<%@ page language="java" contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
    String role = (String) session.getAttribute("role"); 
    String loginId = (String) session.getAttribute("loginId");
%>

<html>
<head>
<jsp:include page="/WEB-INF/views/header/header.jsp" />

<meta charset="UTF-8">
<title>일정 등록</title>

<style>
body { font-family: 'Segoe UI', sans-serif; background-color: #f4f6f8; margin:0; padding:0; }
.container { max-width:700px; margin:50px auto; background:#fff; border-radius:12px; padding:30px 40px; box-shadow:0 8px 20px rgba(0,0,0,0.08); }
h2 { text-align:center; margin-bottom:25px; color:#333; }
label { font-weight:600; color:#555; margin-top:15px; display:block; }
input, textarea, select { width:100%; padding:10px 12px; margin-top:6px; border:1px solid #ccc; border-radius:8px; font-size:14px; background-color:#fafafa; transition:all 0.2s ease; }
input:focus, textarea:focus, select:focus { outline:none; border-color:#4CAF50; background-color:#fff; box-shadow:0 0 5px rgba(76,175,80,0.4); }
textarea { resize:vertical; min-height:100px; }
.btn-submit { margin-top:25px; width:100%; padding:12px; background:linear-gradient(90deg, #4CAF50, #45a049); border:none; border-radius:8px; color:white; font-size:16px; font-weight:bold; cursor:pointer; transition:background 0.3s ease; }
.btn-submit:hover { background:linear-gradient(90deg, #45a049, #3e8e41); }
</style>

<script>
function onCalendarTypeChange() {
    const type = document.getElementById("calendarType").value;
    const shareField = document.getElementById("shareEmployeesGroup");
    shareField.style.display = (type === "SHARE") ? "block" : "none";
}

function validateForm() {
    const title = document.getElementById("title").value.trim();
    const start = document.getElementById("startDate").value;
    const end = document.getElementById("endDate").value;
    const calendarType = document.getElementById("calendarType").value;
    const bigCategory = document.getElementById("bigCategorySeq").value;

    if (!title) { alert("제목을 입력하세요."); return false; }
    if (!start || !end) { alert("시작일과 종료일을 입력하세요."); return false; }
    if (new Date(start) > new Date(end)) { alert("종료일은 시작일 이후여야 합니다."); return false; }
    if (!bigCategory) { alert("대분류를 선택하세요."); return false; }
    if (calendarType === "SHARE") {
        const shareList = document.getElementById("shareEmployees").value.trim();
        if (!shareList) { alert("공유 캘린더의 경우 공유할 직원을 입력하세요."); return false; }
    }
    return true;
}
</script>

</head>
<body>
<div class="container">
    <h2>일정 등록</h2>

    <form action='<%= ctxPath%>/Calendar/addCalendarForm' method="post" onsubmit="return validateForm();">

        <!-- 대분류 선택 -->
        <label>대분류 선택</label>
        <select name="bigCategorySeq" id="bigCategorySeq" required>
            <option value="">-- 선택하세요 --</option>
            <c:forEach var="bigCat" items="${bigCategoryList}">
                <option value="${bigCat.bigCategorySeq}">${bigCat.bigCategoryName}</option>
            </c:forEach>
        </select>

		

        <!-- 소분류 선택 -->
        <label>소분류 선택</label>
        <select name="smallCategorySeq" id="smallCategorySeq">
            <option value="">-- 선택하세요 --</option>
            <c:forEach var="smallCat" items="${smallCategoryList}">
                <option value="${smallCat.smallCategorySeq}">${smallCat.smallCategoryName}</option>
            </c:forEach>
        </select>

        <!-- 일정 제목 -->
        <label>일정 제목</label>
        <input type="text" id="title" name="title" placeholder="일정 제목을 입력하세요" required>

        <!-- 일정 내용 -->
        <label>일정 내용</label>
        <textarea name="content" placeholder="세부 내용을 입력하세요"></textarea>

        <!-- 시작/종료 날짜 -->
        <label>시작 날짜</label>
        <input type="datetime-local" id="startDate" name="startDate" required>
        <label>종료 날짜</label>
        <input type="datetime-local" id="endDate" name="endDate" required>

        <!-- 캘린더 타입 -->
        <label>캘린더 선택</label>
        <select name="calendarType" id="calendarType" onchange="onCalendarTypeChange()" required>
            <c:if test="${role == 'ADMIN'}">
                <option value="COMPANY">사내 캘린더</option>
            </c:if>
            <c:if test="${role == 'TEAM_LEADER'}">
                <option value="DEPARTMENT">부서 캘린더</option>
            </c:if>
            <option value="PERSONAL">내 캘린더</option>
            <option value="SHARE">공유 캘린더</option>
        </select>

        <!-- 공유 캘린더 입력 -->
        <div id="shareEmployeesGroup" style="display:none;">
            <label>공유할 직원</label>
            <input type="text" id="shareEmployees" name="shareEmployees" placeholder="사원번호 또는 사원이름, 쉼표(,)로 구분">
        </div>

        <!-- 반복 -->
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

<jsp:include page="../footer/footer.jsp" />
</body>
</html>
