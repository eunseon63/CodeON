<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/views/header/header.jsp" />


<style type="text/css">
    
    table#calendar {
        margin-top: 70px;
    }
    
    table#calendar th, td {
        padding: 10px 5px;
        vertical-align: middle;
    }
    a {
        color: #395673;
        text-decoration: none;
        cursor: pointer;
    }
    a:hover {
        font-weight: bold;
    }
    
    button.btn_normal {
        background-color: #0071bd;
        border: none;
        color: white;
        width: 70px;
        height: 30px;
        font-size: 12pt;
        padding: 3px 0px;
        margin-right: 10px;
        border-radius: 10%;
    }
</style>

<script type="text/javascript" src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>
<script type="text/javascript">

</script>

<div style="margin-left: 80px; width: 88%;">
    <h3 style="display: inline-block;">일정 상세보기</h3>
    &nbsp;&nbsp;<a href="<%= ctxPath%>/Calendar/list">◀캘린더로 돌아가기</a> 

    <table id="calendar" class="table table-bordered">
        <tr>
            <th style="width: 160px;">일자</th>
            <td>
                <span id="startdate"><c:out value="${requestScope.map.STARTDATE}" /></span>
                &nbsp;~&nbsp;
                <span id="enddate"><c:out value="${requestScope.map.ENDDATE}" /></span>
                &nbsp;&nbsp;<input type="checkbox" id="allDay" disabled/>&nbsp;종일
            </td>
        </tr>
        <tr>
            <th>제목</th>
            <td><c:out value="${requestScope.map.SUBJECT}" /></td>
        </tr>
        <tr>
            <th>캘린더종류</th>
            <td>
                <c:choose>
                    <c:when test="${requestScope.map.TYPE eq '사내'}">사내 일정 - <c:out value="${requestScope.map.SMCATGONAME}" /></c:when>
                    <c:when test="${requestScope.map.TYPE eq '부서'}">부서 일정 - <c:out value="${requestScope.map.SMCATGONAME}" /></c:when>
                    <c:when test="${requestScope.map.TYPE eq '개인'}">개인 일정 - <c:out value="${requestScope.map.SMCATGONAME}" /></c:when>
                    <c:when test="${requestScope.map.TYPE eq '공유'}">공유 일정 - <c:out value="${requestScope.map.SMCATGONAME}" /></c:when>
                    <c:otherwise>기타 - <c:out value="${requestScope.map.SMCATGONAME}" /></c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>장소</th>
            <td><c:out value="${requestScope.map.PLACE}" /></td>
        </tr>
        <tr>
            <th>공유자</th>
            <td><c:out value="${requestScope.map.JOINUSER}" /></td>
        </tr>
        <tr>
            <th>내용</th>
            <td>
                <textarea id="content" rows="10" cols="100" style="height:200px; border:none;" readonly>
					<c:out value="${requestScope.map.CONTENT}" />
                </textarea>
            </td>
        </tr>
        <tr>
            <th>작성자</th>
            <td><c:out value="${requestScope.map.NAME}" /></td>
        </tr>
    </table>

    <c:set var="v_fk_userid" value="${requestScope.map.FK_USERID}" />
    <c:set var="v_loginuser_memberUserid" value="${sessionScope.loginuser.memberUserid}" />
    <c:set var="v_type" value="${requestScope.map.TYPE}" />

    <div style="float: right;">
        <c:choose>
            <c:when test="${v_type eq '사내' && sessionScope.loginuser.gradelevel == 10}">
                <button type="button" class="btn_normal" onclick="editCalendar('${requestScope.map.CALENDARSEQ}')">수정</button>
                <button type="button" class="btn_normal" onclick="delCalendar('${requestScope.map.CALENDARSEQ}')">삭제</button>
            </c:when>
            <c:when test="${v_type eq '개인' && v_fk_userid eq v_loginuser_memberUserid}">
                <button type="button" class="btn_normal" onclick="editCalendar('${requestScope.map.CALENDARSEQ}')">수정</button>
                <button type="button" class="btn_normal" onclick="delCalendar('${requestScope.map.CALENDARSEQ}')">삭제</button>
            </c:when>
        </c:choose>
        <button type="button" class="btn_normal" style="background-color:#990000;" onclick="location.href='<%= ctxPath%>/Calendar/list'">취소</button>
    </div>
</div>

<form name="goEditFrm">
    <input type="hidden" name="calendarseq"/>
    <input type="hidden" name="gobackURL_detailCalendar" value="${requestScope.gobackURL_detailCalendar}"/>
</form>

<jsp:include page="../footer/footer.jsp" />
