<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String ctxPath = request.getContextPath();
%>



<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>사내 게시판</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            padding: 20px;
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
                <li class="nav-item ms-3"><a href="#" class="nav-link">사내게시판 이동</a></li>
                <li class="nav-item fw-bold mt-3">본인이 속한 부서 게시판(개발팀,영업팀)</li>  
                  <li class="nav-item ms-3"><a href="#" class="nav-link">부서게시판 이동</a></li> 
            </ul>
        </div>

        <!-- Main Content -->
        <div class="col-md-10">
          <div class="d-flex justify-content-between align-items-center mt-4 mb-3">
    		<h5 class="mb-0 fw-bold">사내게시판</h5>
			<button type="button" class="btn btn-success" onclick="location.href='<%= ctxPath %>/CodeON/board/add'">글쓰기</button>
	
		</div>
			 
			 <br>
			 <br>
			 <br>
			 <br>
			 
			 
            <!-- 게시글 목록 -->	
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>글쓴이</th>
                        <th>날짜</th>
                        <th>조회수</th>
                        <th>첨부</th>
                        <th>댓글</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- 반복 렌더링 예시 -->
                    <tr>
                        <td>1</td>
                        <td><a href="#">[공지] 휴가 일정 안내</a></td>
                        <td>인사팀</td>
                        <td>2025-08-08</td>
                        <td>123</td>
                        <td><i class="bi bi-paperclip file-icon"></i></td>
                        <td><span class="comment-count">5</span></td>
                    </tr>
                    <!-- ... -->
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
        
         <!-- 검색 -->
          <form class="form-inline my-3 d-flex justify-content-center"method="get">
            
               <select class="form-control me-2" id="searchType" style="width: 120px;">
                    <option value="title">제목</option>
                    <option value="content">내용</option>
                    <option value="title_content">제목+내용</option>
                    <option value="writer">글쓴이</option>
                </select>
				<input type="text" class="form-control me-2" placeholder="검색어 입력" id="searchInput" style="width: 500px;" />
                <button type="submit" class="btn btn-primary">검색</button>
                

            </form>
			
			
        
        </div>
    </div>
</div>




