<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

<%
    String ctxPath = request.getContextPath();
%>

<style>
    body { padding-top: 70px; }
    .board-content { white-space: pre-line; }
    .reply-row div {
        border-left: 2px solid #dee2e6;
        padding-left: 10px;
        margin-bottom: 5px;
    }
</style>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<div class="container mt-5">
    <!-- 게시글 상세 -->
    <div class="card mb-4 shadow-sm">
        <div class="card-header bg-light">
            <h4 class="mb-1">${board.boardTitle}</h4>
            <small class="text-muted">
                작성자: ${board.memberName} | 
                <fmt:formatDate value="${board.boardRegdate}" pattern="yyyy-MM-dd HH:mm"/> | 
                조회수: ${board.boardReadcount}
            </small>
             <% if(session.getAttribute("loginuser") != null) { %>
             <button type="button" class="btn btn-primary btn-sm" onclick="goEdit(${board.boardSeq})">수정</button>
            <button type="button" class="btn btn-danger btn-sm" onclick="goDelete(${board.boardSeq})">삭제</button>
        <% } %>
        </div>
        
        <div class="card-body">
            <p class="board-content">${board.boardContent}</p>

            <!-- 첨부파일 -->
            <c:if test="${not empty board.boardFileSaveName}">
                <c:choose>
                    <c:when test="${fn:endsWith(board.boardFileSaveName, '.jpg') 
                                or fn:endsWith(board.boardFileSaveName, '.png') 
                                or fn:endsWith(board.boardFileSaveName, '.gif')}">
                        <div class="mt-3">
                         <img src="${ctxPath}/resources/upload/${board.boardFileSaveName}" 
    						 alt="${board.boardFileOriName}" class="img-fluid rounded">
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="mt-3">
                            <i class="bi bi-paperclip"></i>
                            <a href="${ctxPath}/board/download?fileName=${board.boardFileSaveName}">
                                ${board.boardFileOriName}
                            </a>
                        </p>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </div>
    </div>

    <!-- 댓글 입력 폼 -->
    <form id="frmComment" class="mb-3">
        <input type="hidden" id="boardSeq" value="${board.boardSeq}" />
        <textarea id="commentContent" class="form-control mb-2" rows="3" placeholder="댓글을 입력하세요"></textarea>
        <% if(session.getAttribute("loginuser") != null) { %>
            <button type="button" class="btn btn-primary" onclick="goWriteComment()">등록</button>
        <% } %>
    </form>

    <!-- 댓글 목록 -->
    <table class="table table-bordered">
        <thead>
            <tr>
                <th>No</th>
                <th>내용</th>
                <th>작성자</th>
                <th>작성일</th>
                <th>reply/수정/삭제</th>
            </tr>
        </thead>
        <tbody id="commentDisplay">
           
        </tbody>
    </table>
</div>

<script>
const isLogin = <%= (session.getAttribute("loginuser") != null) %>;

function goWriteComment() {
    const content = $("#commentContent").val().trim();
    if(!content) { alert("댓글 내용을 입력하세요."); return; }

    $.ajax({
        url: "<%= ctxPath %>/comment/add",
        type: "POST",
        data: { fkBoardSeq: $("#boardSeq").val(), commentContent: content },
        success: function(json) {
            if(json === "success") {
                $("#commentContent").val("");
                goReadComment(1);
            } else {
                alert("댓글 등록 실패");
            }
        }
    });
}

function goReadComment(currentShowPageNo) {
    $.ajax({
        url: "<%= ctxPath %>/comment/list",
        type: "GET",
        data: { fkBoardSeq: $("#boardSeq").val() },
        dataType: "json",
        success: function(json) {
            let v_html = "";
            if(json.length > 0) {
                $.each(json, function(index, item) {
                    v_html += `
                        <tr id="comment-\${item.commentSeq}">
                            <td>\${index + 1}</td>
                            <td>
                                <div id="commentContentDisplay-\${item.commentSeq}">\${item.commentContent}</div>
                                <div id="commentEditArea-\${item.commentSeq}" style="display:none;">
                                    <textarea id="commentContent-\${item.commentSeq}" class="form-control mb-2" rows="2"></textarea>
                                    <button class="btn btn-primary btn-sm" onclick="submitEditComment(\${item.commentSeq})">저장</button>
                                    <button class="btn btn-secondary btn-sm" onclick="cancelEditComment(\${item.commentSeq})">취소</button>
                                </div>
                            </td>
                            <td>\${item.memberName}</td>
                            <td>\${item.commentRegdate}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-secondary" onclick="showReplyForm(\${item.commentSeq})">reply</button>`;
                    if(item.mine) {
                        v_html += `
                                <button class="btn btn-sm btn-outline-primary" onclick="startEditComment(\${item.commentSeq})">수정</button>
                                <button class="btn btn-sm btn-outline-danger" onclick="goDeleteComment(\${item.commentSeq})">삭제</button>`;
                    }
                    v_html += `
                            </td>
                        </tr>
                        <tr id="reply-area-\${item.commentSeq}" class="reply-row" style="display:none;">
                            <td colspan="5">
                                <textarea id="replyContent-\${item.commentSeq}" class="form-control mb-2" rows="2" placeholder="대댓글 입력"></textarea>
                                <button class="btn btn-primary btn-sm" onclick="goWriteReply(\${item.commentSeq})">등록</button>
                                <div id="reply-list-\${item.commentSeq}" class="mt-2"></div>
                            </td>
                        </tr>
                    `;
                });
            } else {
                v_html = `<tr><td colspan='5'>댓글이 없습니다</td></tr>`;
            }
            $("#commentDisplay").html(v_html);

            // 대댓글도 로딩
            $.each(json, function(_, item){
                loadReplyList(item.commentSeq);
            });
        }
    });
}

//댓글 수정 관련
function startEditComment(commentSeq) {
    $("#commentContent-" + commentSeq).val(""); // 빈 textarea
    $("#commentContentDisplay-" + commentSeq).hide();
    $("#commentEditArea-" + commentSeq).show();
}

function cancelEditComment(commentSeq) {
    $("#commentEditArea-" + commentSeq).hide();
    $("#commentContentDisplay-" + commentSeq).show();
}

function submitEditComment(commentSeq) {
    const content = $("#commentContent-" + commentSeq).val().trim();
    if(!content) { 
        alert("댓글 내용을 입력하세요."); 
        return; 
    }

    $.ajax({
        url: "<%= ctxPath %>/comment/edit",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            commentSeq: commentSeq,
            commentContent: content
        }),
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
               
                goReadComment(1);
            } else {
                alert(json.message || "댓글 수정 실패");
            }
        },
        error: function() {
            alert("댓글 수정 중 오류가 발생했습니다.");
        }
    });
}


function showReplyForm(commentSeq) {
    $("#reply-area-" + commentSeq).toggle();
}

function goWriteReply(parentCommentSeq) {
    if(!isLogin) { alert("로그인 후 사용 가능합니다."); return; }
    const content = $("#replyContent-" + parentCommentSeq).val().trim();
    if(!content) { alert("내용을 입력하세요"); return; }

    $.ajax({
        url: "<%= ctxPath %>/comment/addReply",
        type: "POST",
        data: {
            parentCommentSeq: parentCommentSeq,
            fkBoardSeq: $("#boardSeq").val(),
            commentContent: content
        },
        success: function(json) {
            if(json === "success") {
                $("#replyContent-" + parentCommentSeq).val("");
                loadReplyList(parentCommentSeq);
            } else {
                alert("대댓글 등록 실패");
            }
        }
    });
}

// 대댓글 리스트 로딩
function loadReplyList(parentCommentSeq) {
    $.ajax({
        url: "<%= ctxPath %>/comment/listReply",
        type: "GET",
        data: { parentCommentSeq: parentCommentSeq },
        dataType: "json",
        success: function(json) {
            let html = "";

            if(json.length > 0) {
                $.each(json, function(i, reply) {
                    html += '<div class="border-start ps-3 mb-1">';
                    html += '<strong>' + reply.memberName + '</strong> : ' + reply.replyContent;
                    html += ' <small class="text-muted">(' + reply.replyRegdate + ')</small>';

                    // 수정/삭제 버튼 영역
                    if(reply.mine) {
                        html += ` <button class="btn btn-sm btn-outline-primary" onclick="startEditReply(\${reply.replySeq})">수정</button>`;
                        html += ` <button class="btn btn-sm btn-outline-danger" onclick="goDeleteReply(\${reply.replySeq}, \${parentCommentSeq})">삭제</button>`;
                    }

                    // 수정용 textarea + 버튼 (초기 숨김)
                    html += `<div id="replyEditArea-\${reply.replySeq}" style="display:none; margin-top:5px;">
                                <textarea id="replyEditContent-\${reply.replySeq}" class="form-control mb-1" rows="2"></textarea>
                                <button class="btn btn-primary btn-sm" onclick="submitEditReply(\${reply.replySeq}, \${parentCommentSeq})">저장</button>
                                <button class="btn btn-secondary btn-sm" onclick="cancelEditReply(\${reply.replySeq})">취소</button>
                            </div>`;

                    html += '</div>';
                });
            } else {
                html = "<small class='text-muted'>대댓글이 없습니다.</small>";
            }

            $("#reply-list-" + parentCommentSeq).html(html);
        }
    });
}

//글 수정
function goEdit(boardSeq) {
    location.href = "<%= ctxPath %>/board/edit?boardSeq=" + boardSeq;
}

//대댓글 수정 관련
function startEditReply(replySeq) {
    $("#replyEditContent-" + replySeq).val(""); // 빈 textarea
    $("#replyContentDisplay-" + replySeq).hide();
    $("#replyEditArea-" + replySeq).show();
}

function cancelEditReply(replySeq) {
    $("#replyEditArea-" + replySeq).hide();
    $("#replyContentDisplay-" + replySeq).show();
}

function submitEditReply(replySeq, parentCommentSeq) {
    const content = $("#replyEditContent-" + replySeq).val().trim();
    if(!content) { 
        alert("대댓글 내용을 입력하세요."); 
        return; 
    }

    $.ajax({
        url: "<%= ctxPath %>/comment/editReply", //  대댓글 수정
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            replySeq: replySeq,
            replyContent: content
        }),
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
                
                loadReplyList(parentCommentSeq);
            } else {
                alert(json.message || "대댓글 수정 실패");
            }
        },
        error: function() {
            alert("대댓글 수정 중 오류가 발생했습니다.");
        }
    });
}


// 글 삭제
function goDelete(boardSeq) {
    if(!confirm("정말로 삭제하시겠습니까?")) return;

    $.ajax({
        url: "<%= ctxPath %>/board/delete",
        type: "POST",
        data: { boardSeq: boardSeq },
        dataType: "json", // 서버에서 JSON으로 응답 받음
        success: function(json) {
            if(json.status === "success") {
                alert(json.message);
                location.href = "<%= ctxPath %>/board/list";
            } else {
                alert(json.message);
            }
        },
        error: function() {
            alert("오류 발생");
        }
    });
}

function goDeleteComment(commentSeq) {
    if(!confirm("정말로 이 댓글을 삭제하시겠습니까?")) return;

    $.ajax({
        url: "<%= ctxPath %>/comment/delete",
        type: "POST",
        data: { commentSeq: commentSeq },
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
                alert(json.message);
                $("#comment-" + commentSeq).remove(); // 댓글 행 삭제
                $("#reply-area-" + commentSeq).remove(); // 대댓글 영역 삭제, 전부 삭제 
            } else {
                alert(json.message);
            }
        },
        error: function() {
            alert("댓글 삭제 중 오류가 발생했습니다.");
        }
    });
}

function goDeleteReply(replySeq, parentCommentSeq) {
    if(!confirm("정말로 이 대댓글을 삭제하시겠습니까?")) return;

    $.ajax({
        url: "<%= ctxPath %>/comment/deleteReply",
        type: "POST",
        data: { replySeq: parseInt(replySeq) },
        dataType: "json",
        success: function(json) {
            if(json.status === "success") {
                alert(json.message);
                // 삭제 후 부모 댓글의 대댓글 목록 다시 로딩
                loadReplyList(parentCommentSeq);
            } else {
                alert(json.message);
            }
        },
        error: function() {
            alert("대댓글 삭제 중 오류가 발생했습니다.");
        }
    });
}
$(document).ready(function() {
    goReadComment(1);
});
</script>
