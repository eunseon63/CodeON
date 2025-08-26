<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시판 목록</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    
    <style>
        body {
            padding-top: 70px; 
            padding-left: 20px;
            padding-right: 20px;
        }
        .sidebar {
            min-width: 200px;
        }
        .file-icon {
            color: #007bff;
        }
        .comment-count {
            color: gray;
            font-size: 0.9em;
        }
    </style>
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <div class="col-md-2 sidebar border-end">
            <h5 class="mt-3">게시판</h5>
            <ul class="nav flex-column">
                <li class="nav-item ms-3"><a href="${ctxPath}/board/list?fkBoardTypeSeq=0" class="nav-link">사내게시판 이동</a></li>
                <li class="nav-item ms-3"><a href="${ctxPath}/board/list?fkBoardTypeSeq=1" class="nav-link">부서게시판 이동</a></li>
            </ul>
        </div>

        <!-- Main Content -->
        <div class="col-md-10">
            <!-- 게시판 유형 버튼 -->
            <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
                <div>
                    <a href="${ctxPath}/board/list?fkBoardTypeSeq=0" 
                       class="btn ${param.fkBoardTypeSeq=='0'?'btn-primary':'btn-outline-primary'}">사내게시판</a>
                    <a href="${ctxPath}/board/list?fkBoardTypeSeq=1" 
                       class="btn ${param.fkBoardTypeSeq=='1'?'btn-primary':'btn-outline-primary'}">부서게시판</a>
                </div>
                <button type="button" class="btn btn-success" 
                        onclick="location.href='${ctxPath}/board/add?fkBoardTypeSeq=${param.fkBoardTypeSeq}'">글쓰기</button>
            </div>

            <!-- 카테고리 + 검색 -->
            <form class="d-flex align-items-center mb-3" method="get" action="${ctxPath}/board/list">
                <input type="hidden" name="fkBoardTypeSeq" value="${param.fkBoardTypeSeq}" />
                
                
                
                <label class="me-2">카테고리:</label>
                <select name="fkBoardCategorySeq" class="form-select me-3" style="width:150px;">
                    <option value="">전체</option>
                    <option value="0" ${param.fkBoardCategorySeq=='0'?'selected':''}>공지사항</option>
                    <option value="1" ${param.fkBoardCategorySeq=='1'?'selected':''}>일반</option>
                    <option value="2" ${param.fkBoardCategorySeq=='2'?'selected':''}>경조사</option>
                </select>
            
                <select class="form-select me-2" name="searchType" style="width: 150px;">
                    <option value="boardTitle" ${param.searchType=='boardTitle'?'selected':''}>제목</option>
                    <option value="boardContent" ${param.searchType=='boardContent'?'selected':''}>내용</option>
                   
                    <option value="titleContent" ${param.searchType=='titleContent'?'selected':''}>제목+내용</option>
                    <option value="memberName" ${param.searchType=='memberName'?'selected':''}>글쓴이</option>
                </select>

                <input type="text" name="searchword" value="${param.searchword}" 
                       class="form-control me-2" placeholder="검색어 입력" style="width: 300px;" />
                <button type="submit" class="btn btn-primary">검색</button>
            </form>

            <!-- 게시글 목록 -->
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>번호</th>
                        <th>카테고리</th>
                        <th>제목</th>
                        <th>글쓴이</th>
                        <th>날짜</th>
                        <th>조회수</th>
                        <th>첨부</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty boardList}">
                            <tr>
                                <td colspan="7" class="text-center text-muted">등록된 게시글이 없습니다.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="board" items="${boardList}">
                                <tr>
                                    <td>${board.boardSeq}</td>
                                    <td>${board.boardCategoryName}</td>
                                    <td>
                                        <a href="${ctxPath}/board/view?boardSeq=${board.boardSeq}">
                                            ${board.boardTitle}
                                        </a>
                                        <span class="badge bg-secondary">${board.commentCount}</span>
                                    </td>
                                    <td>${board.memberName}</td>
                                    <td>
                                        <fmt:formatDate value="${board.boardRegdate}" pattern="yyyy-MM-dd" />
                                    </td>
                                    <td>${board.boardReadcount}</td>
                                    <td>
                                        <c:if test="${not empty board.boardFileSaveName}">
                                            <i class="bi bi-paperclip file-icon"></i>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- 페이지네이션 -->
            <div class="text-center mt-4">
                ${pageBar}
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/footer/footer.jsp" />
</body>
</html>
