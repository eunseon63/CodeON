<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
String ctxPath = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>결재라인 불러오기</title>

<style>
:root {
	--bg: #f6f7fb;
	--card: #fff;
	--text: #111;
	--muted: #6b7280;
	--line: #e5e7eb;
	--brand: #2563eb;
	--brand-100: #e8eefc;
	--danger: #ef4444;
	--radius: 16px;
}

* {
	box-sizing: border-box
}

body {
	margin: 0;
	background: var(--bg);
	color: var(--text);
	font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial;
}

.wrap {
	max-width: 1100px;
	margin: 32px auto;
	padding: 0 20px;
}

.title {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 16px;
}

.title h1 {
	font-size: 22px;
	margin: 0;
}

.controls {
	display: flex;
	gap: 10px;
	flex-wrap: wrap;
	margin-bottom: 16px;
}

.input, .select {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 10px;
	height: 38px;
	padding: 0 12px;
	outline: none;
}

.input {
	width: 260px;
}

.btn {
	height: 38px;
	padding: 0 14px;
	border: none;
	border-radius: 10px;
	cursor: pointer;
	background: var(--brand);
	color: #fff;
	font-weight: 600;
}

.btn.secondary {
	background: #fff;
	color: #111;
	border: 1px solid var(--line);
}

.btn.danger {
	background: var(--danger);
}

.card {
	background: var(--card);
	border: 1px solid var(--line);
	border-radius: var(--radius);
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
}

table {
	width: 100%;
	border-collapse: separate;
	border-spacing: 0;
}

thead th {
	text-align: left;
	font-size: 13px;
	color: var(--muted);
	padding: 14px 16px;
	border-bottom: 1px solid var(--line);
	background: #fafafa;
}

tbody td {
	padding: 14px 16px;
	border-bottom: 1px solid var(--line);
	vertical-align: middle;
}

tbody tr:hover {
	background: #fcfcff;
}

.name {
	font-weight: 600;
}

.chips {
	display: flex;
	gap: 6px;
	flex-wrap: wrap;
}

.chip {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	border: 1px solid var(--line);
	background: #fff;
	border-radius: 999px;
	padding: 4px 10px;
	font-size: 12px;
}

.avatar {
	width: 20px;
	height: 20px;
	border-radius: 50%;
	background: var(--brand-100);
	display: inline-flex;
	align-items: center;
	justify-content: center;
	font-size: 11px;
	font-weight: 700;
	color: #2b3a63;
}

.muted {
	color: var(--muted);
	font-size: 12px;
}

.footer {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 12px 16px;
}

.pagination {
	display: flex;
	gap: 8px;
}

.page-btn {
	min-width: 36px;
	height: 36px;
	border: 1px solid var(--line);
	background: #fff;
	border-radius: 10px;
	cursor: pointer;
}

.empty {
	text-align: center;
	padding: 40px 16px;
	color: var(--muted);
}
/* sticky header for usability */
.table-wrap {
	overflow: auto;
	border-radius: 0 0 var(--radius) var(--radius);
}

thead th {
	position: sticky;
	top: 0;
	z-index: 1;
}
</style>

</head>

<script type="text/javascript">

$(function(){
	
	loadMembers();
	
});

function loadMembers() {
	
}

</script>

<body>

<div class="wrap">
    <div class="title">
      <h1>결재라인 불러오기</h1>
      <div class="muted">환경설정 &rsaquo; 결재라인</div>
    </div>

    <div class="card">
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th style="width:56px;">선택</th>
              <th>템플릿명</th>
              <th>결재선 미리보기</th>
              <th style="width:120px;">결재자수</th>
              <th style="width:150px;">등록일</th>
            </tr>
          </thead>
          <tbody id="tbody">
            <tr><td colspan="5" class="empty">불러오는 중…</td></tr>
          </tbody>
        </table>
      </div>
      <div class="footer">
        <div class="pagination">
          <button id="prev" class="page-btn">&laquo;</button>
          <span id="pageInfo" class="muted" style="padding:0 6px;"></span>
          <button id="next" class="page-btn">&raquo;</button>
        </div>
        <div style="display:flex; gap:8px;">
          <button id="useSelected" class="btn">선택한 결재라인 불러오기</button>
          <button id="closeWin" class="btn secondary">닫기</button>
        </div>
      </div>
    </div>
  </div>

</body>
</html>