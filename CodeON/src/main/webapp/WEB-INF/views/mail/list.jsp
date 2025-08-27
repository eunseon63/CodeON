<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<style>
    .mail-unread { color: #212529; font-weight: bold; }
    .mail-read { color: #6c757d; font-weight: normal; }
    .table-hover tbody tr:hover td { background-color: #f1f3f5; }
    .icon-cell { width: 40px; text-align: center; }
    .star { cursor: pointer; font-size: 1.2rem; color: #dcdcdc; }
    .star.important { color: #ffc107; }
</style>

<script>
    function toggleStar(element, emailSeq) {
        element.classList.toggle('important');
        // AJAX 호출로 서버에 중요메일 상태 저장 가능
        console.log('메일 번호 ' + emailSeq + ' 중요 상태 토글됨');
    }
</script>

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
                <table class="table table-hover mb-0 align-middle">
                    <thead class="table-light">
                        <tr>
                            <th scope="col"></th> <!-- 중요메일 별표 -->
                            <th scope="col"></th> <!-- 메일 아이콘 -->
                            <th scope="col">보낸 사람</th>
                            <th scope="col">제목</th>
                            <th scope="col">날짜</th>
                            <th scope="col"></th> <!-- 첨부파일 -->
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mail" items="${mailList}">
                            <tr style="cursor:pointer;" onclick="viewMail(${mail.emailSeq})">
                                <!-- 중요메일 별표 -->
                                <td class="icon-cell">
                                    <i class="bi bi-star star ${mail.emailSendImportant == 1 ? 'important' : ''}" 
                                       onclick="event.stopPropagation(); toggleStar(this, ${mail.emailSeq});"></i>
                                </td>

                                <!-- 메일 아이콘 -->
                                <td class="icon-cell">
                                    <c:choose>
                                        <c:when test="${mail.emailReadStatus == 0}">
                                            <i class="bi bi-envelope-fill"></i>
                                        </c:when>
                                        <c:otherwise>
                                            <i class="bi bi-envelope-open-fill"></i>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>${mail.sendMemberEmail}</td>

                                <!-- 제목 색상 -->
                                <td class="${mail.emailReadStatus == 0 ? 'mail-unread' : 'mail-read'}">
                                    ${mail.emailTitle}
                                </td>

                                <td>${mail.emailRegdate}</td>

                                <!-- 첨부파일 아이콘 -->
                                <td class="icon-cell">
                                    <c:if test="${not empty mail.emailFilename}">
                                        <i class="bi bi-paperclip"></i>
                                    </c:if>
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
