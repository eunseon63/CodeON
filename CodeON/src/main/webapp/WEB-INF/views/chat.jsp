<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>DB RAG Chatbot</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
</head>

<jsp:include page="header/header.jsp" />
<jsp:include page="admin/adminsidebar.jsp" />

<script>
$(document).ready(function() {
	
    $.ajax({
        url: '/ai/index',
        method: 'GET',
        success: function(data) {
            console.log(data); // 콘솔에 인덱싱 완료 메시지
        },
        error: function(xhr, status, error) {
            console.error('인덱싱 오류:', error);
        }
    });
	
    $('#btnSend').click(function() {
        const question = $('#question').val().trim();
        if (!question) {
            alert('질문을 입력해주세요.');
            return;
        }

        $('#response').text('응답을 기다리는 중...');

        $.ajax({
            url: '/ai/chat',
            method: 'GET',
            data: { question: question },
            success: function(data) {
                $('#response').text(data);
            },
            error: function(xhr, status, error) {
                $('#response').text('오류 발생: ' + error);
            }
        });
    });
});
</script>


<main style="margin-left: 240px; padding-top: 20px;">
	<div class="container mt-5">
	    <h2 class="mb-4">인사 도우미</h2>
	
	    <div class="form-group">
	        <label for="question">질문 입력:</label>
	        <input type="text" class="form-control" id="question" placeholder="질문을 입력하세요">
	    </div>
	
	    <button class="btn btn-primary" id="btnSend">전송</button>
	
	    <hr>
	
	    <h4>답변:</h4>
	    <pre id="response" class="border p-3 bg-light" style="white-space: pre-wrap;"></pre>
	</div>
</main>

<jsp:include page="footer/footer.jsp" />
