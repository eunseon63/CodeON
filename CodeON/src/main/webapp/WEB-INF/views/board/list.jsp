<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctxPath = request.getContextPath();
%>

<jsp:include page="../header/header.jsp" />

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
                <li class="nav-item fw-bold">사내 게시판</li>
                <li class="nav-item ms-3"><a href="${ctxPath}/board/list?fk_board_type_seq=0" class="nav-link">사내게시판 이동</a></li>
                <li class="nav-item fw-bold mt-3">본인이 속한 부서 게시판</li>  
                <li class="nav-item ms-3"><a href="${ctxPath}/board/list?fk_board_type_seq=1" class="nav-link">부서게시판 이동</a></li> 
            </ul>
        </div>

        <!-- Main Content -->
        <div class="col-md-10">
            <!-- 게시판 유형 버튼 -->
            <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
                <div>
                    <a href="${ctxPath}/board/list?fk_board_type_seq=0" class="btn ${param.fk_board_type_seq=='0'?'btn-primary':'btn-outline-primary'}">사내게시판</a>
                    <a href="${ctxPath}/board/list?fk_board_type_seq=1" class="btn ${param.fk_board_type_seq=='1'?'btn-primary':'btn-outline-primary'}">부서게시판</a>
                </div>
                <button type="button" class="btn btn-success" onclick="location.href='${ctxPath}/board/add?fk_board_type_seq=${param.fk_board_type_seq}'">글쓰기</button>
            </div>

            <!-- 카테고리 필터 -->
            <form class="d-flex align-items-center mb-3" method="get" action="${ctxPath}/board/list">
                <input type="hidden" name="fk_board_type_seq" value="${param.fk_board_type_seq}" />
                <label class="me-2">카테고리:</label>
                <select name="fk_board_category_seq" class="form-select me-3" style="width:150px;">
                    <option value="">전체</option>
                    <option value="0" ${param.fk_board_category_seq=='0'?'selected':''}>공지사항</option>
                    <option value="1" ${param.fk_board_category_seq=='1'?'selected':''}>일반</option>
                    <option value="2" ${param.fk_board_category_seq=='2'?'selected':''}>경조사</option>
                </select>

                <!-- 검색 -->
                <select class="form-select me-2" name="searchType" style="width: 150px;">
                    <option value="board_title" ${param.searchType=='board_title'?'selected':''}>제목</option>
                    <option value="board_content" ${param.searchType=='board_content'?'selected':''}>내용</option>
                    <option value="title_content" ${param.searchType=='title_content'?'selected':''}>제목+내용</option>
                    <option value="member_name" ${param.searchType=='member_name'?'selected':''}>글쓴이</option>
                </select>
                <input type="text" name="keyword" value="${param.keyword}" class="form-control me-2" placeholder="검색어 입력" style="width: 300px;" />
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
                        <th>댓글</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="board" items="${boardList}">
                        <tr>
                            <td>${board.board_seq}</td>
                            <td>${board.board_category_name}</td>
                            <td>
                                <a href="${ctxPath}/board/view?board_seq=${board.board_seq}">
                                    ${board.board_title}
                                </a>
                            </td>
                            <td>${board.member_name}</td>
                            <td>${board.board_regdate}</td>
                            <td>${board.board_readcount}</td>
                            <td><c:if test="${not empty board.board_file_save_name}"><i class="bi bi-paperclip file-icon"></i></c:if></td>
                            <td><span class="comment-count">${board.comment_count}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- 페이지네이션 -->
            <div class="d-flex justify-content-center mt-4">
                <nav>
                    <ul class="pagination">
                        <li class="page-item disabled"><a class="page-link" href="#">이전</a></li>
                        <li class="page-item active"><a class="page-link" href="#">1</a></li>
                        <li class="page-item"><a class="page-link" href="#">2</a></li>
                        <li class="page-item"><a class="page-link" href="#">다음</a></li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/footer/footer.jsp" />
</body>
</html>
