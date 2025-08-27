<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="mailsidebar.jsp" />

<main style="margin-left: 240px; padding-top: 20px;">
    <div class="container-fluid">
        <h3 class="mb-4">받은 메일함</h3>

        <!-- 검색 카드 -->
        <div class="card mb-4 shadow-sm">
            <div class="card-body row g-2 align-items-center">
                <div class="col-md-8 col-sm-12">
                    <input type="text" id="searchKeyword" class="form-control" placeholder="메일 검색...">
                </div>
                <div class="col-md-4 col-sm-12 text-md-end mt-2 mt-md-0">
                    <button class="btn btn-primary" id="btnSearch">검색</button>
                </div>
            </div>
        </div>

        <!-- 메일 테이블 카드 -->
        <div class="card shadow-sm">
            <div class="card-body p-0">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col">보낸 사람</th>
                            <th scope="col">제목</th>
                            <th scope="col">날짜</th>
                            <th scope="col">상태</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mail" items="${mailList}">
                            <tr style="cursor:pointer;" onclick="viewMail(${mail.emailSeq})">
                                <td>${mail.sendMemberEmail}</td>
                                <td>${mail.emailTitle}</td>
                                <td>${mail.emailRegdate}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${mail.emailReadStatus == 1}">
                                            <span class="badge bg-secondary">읽음</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-primary">안읽음</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../footer/footer.jsp" />
