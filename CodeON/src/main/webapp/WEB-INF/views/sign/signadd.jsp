<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%
String ctxPath = request.getContextPath();
%>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="signsidebar.jsp" />

<style>
:root {
	--header-h: 70px;
	--sidebar-w: 220px;
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

.main-content {
	margin-left: var(--sidebar-w);
	padding: 20px 28px 64px;
	min-height: 100vh;
	box-sizing: border-box;
}

.page-wrap {
	max-width: 1200px;
	margin: 24px auto;
	padding: 0 16px;
	box-sizing: border-box
}

.topbar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 16px
}

.h1 {
	margin: 0;
	font-size: 22px;
	font-weight: 800
}

.btn {
	height: 36px;
	padding: 0 14px;
	border-radius: 10px;
	border: 1px solid var(--line);
	background: #fff;
	cursor: pointer
}

.btn.brand {
	border-color: var(--brand);
	background: var(--brand);
	color: #fff
}

.btn.ghost {
	background: #fff
}

.badge-red {
	display: inline-flex;
	gap: 8px;
	align-items: center;
	font-size: 13px;
	color: var(--danger)
}

.grid {
	display: grid;
	grid-template-columns: 1fr 1.1fr;
	gap: 14px
}

.card {
	background: var(--card);
	border: 1px solid var(--line);
	border-radius: var(--radius);
	overflow: hidden
}

.card-h {
	padding: 12px 14px;
	border-bottom: 1px solid var(--line);
	font-weight: 700
}

.card-b {
	padding: 14px
}

.row2 {
	display: grid;
	grid-template-columns: 120px 1fr;
	gap: 10px;
	align-items: center
}

.input, .select, .date, .number, .textarea {
	height: 40px;
	border: 1px solid var(--line);
	border-radius: 10px;
	padding: 0 12px;
	box-sizing: border-box;
	width: 100%;
}

.textarea {
	height: 180px;
	padding: 10px 12px;
	resize: vertical
}

.tbl {
	width: 100%;
	border-collapse: separate;
	border-spacing: 0 10px
}

.tbl th {
	font-size: 13px;
	color: #374151;
	text-align: left;
	padding: 6px 10px
}

.tbl td {
	background: #fafafa;
	border: 1px solid var(--line);
	border-left: none;
	border-right: none;
	padding: 10px
}

.tbl tr td:first-child {
	border-left: 1px solid var(--line);
	border-top-left-radius: 10px;
	border-bottom-left-radius: 10px
}

.tbl tr td:last-child {
	border-right: 1px solid var(--line);
	border-top-right-radius: 10px;
	border-bottom-right-radius: 10px
}

.tbl .center {
	text-align: center
}

.approval-table {
	width: 100%;
	border-collapse: collapse
}

.approval-table th, .approval-table td {
	border: 1px solid var(--line);
	padding: 8px
}

.approval-table th {
	background: #f9fafb
}

.empty {
	color: var(--muted);
	text-align: center;
	padding: 24px 0
}

/* 카테고리 탭 (라디오 + CSS만) */
.doc-tabs {
	margin-top: 14px
}

.doc-tabs input[type=radio] {
	display: none
}

.tabbar {
	display: flex;
	gap: 8px;
	flex-wrap: wrap
}

.tabbar label {
	cursor: pointer;
	border: 1px solid var(--line);
	background: #fff;
	border-radius: 999px;
	padding: 8px 14px;
	font-size: 14px;
}

#t-proposal:checked ~ .tabbar label[for=t-proposal], #t-vacation:checked 
	~ .tabbar label[for=t-vacation], #t-expense:checked  ~ .tabbar label[for=t-expense],
	#t-trip:checked     ~ .tabbar label[for=t-trip] {
	border-color: var(--brand);
	background: rgba(37, 99, 235, .1);
	color: #1e40af;
	font-weight: 700;
}

.forms .form {
	display: none;
	margin-top: 12px
}

#t-proposal:checked ~ .forms .f-proposal {
	display: block
}

#t-vacation:checked ~ .forms .f-vacation {
	display: block
}

#t-expense:checked  ~ .forms .f-expense {
	display: block
}

#t-trip:checked     ~ .forms .f-trip {
	display: block
}

/* 지출내역 테이블 */
.exp-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 8px
}

.exp-table th, .exp-table td {
	border: 1px solid var(--line);
	padding: 8px;
	text-align: left
}

.exp-table th {
	background: #f9fafb
}

.exp-actions {
	display: flex;
	gap: 8px;
	margin-top: 8px
}

.sum {
	margin-top: 8px;
	color: #111;
	font-weight: 700;
	text-align: right
}

.small {
	height: 32px;
	padding: 0 10px;
	border-radius: 8px
}
</style>

<script type="text/javascript">

</script>

<body>
<div class="main-content">
  <div class="page-wrap">
    <!-- 상단 -->
    <div class="topbar">
      <h1 class="h1">기안문서 작성</h1>
      <div style="display:flex;gap:10px;align-items:center">
        <label class="badge-red">
          <input id="urgent" type="checkbox" style="accent-color:#ef4444" />
          긴급 문서
        </label>
        <button type="button" class="btn ghost" id="btnCancel">취소</button>
        <button type="button" class="btn brand" id="btnSubmit">상신</button>
      </div>
    </div>

    <!-- 문서정보 / 결재라인 -->
    <div class="grid">
      <!-- 문서정보 -->
      <section class="card">
        <div class="card-h">문서정보</div>
        <div class="card-b" style="display:grid;gap:10px">
          <div class="row2">
            <div>기안자</div>
            <input class="input" id="drafter" value="${sessionScope.loginuser.memberName}" readonly>
          </div>
          <div class="row2">
            <div>소속</div>
            <input class="input" id="dept" value="${sessionScope.loginuser.department.deptName}" readonly>
          </div>
          <div class="row2">
            <div>기안일</div>
            <input class="input" id="draftDate" readonly>
          </div>
          <div class="row2">
            <div>문서번호</div>
            <input class="input" id="docNo" placeholder="자동발급 (저장 후 생성)" readonly>
          </div>
        </div>
      </section>

      <!-- 결재라인 -->
      <section class="card">
        <div class="card-h" style="display:flex;justify-content:space-between;align-items:center">
          <span>결재라인</span>
          <div style="display:flex;gap:8px">
            <button type="button" class="btn small" id="btnPickLine">불러오기</button>
            <button type="button" class="btn small" id="btnEditLine">선택하기</button>
          </div>
        </div>
        <div class="card-b">
          <table class="approval-table">
            <thead>
              <tr><th style="width:70px">순서</th><th>소속</th><th style="width:120px">직급</th><th style="width:140px">성명</th></tr>
            </thead>
            <tbody id="apprTbody">
              <tr><td colspan="4" class="empty">결재자를 선택하세요.</td></tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>

    <!-- 카테고리 & 폼 -->
    <section class="card doc-tabs" style="margin-top:14px">
      <div class="card-h">결재 양식</div>
      <div class="card-b">
        <!-- 탭 라디오 -->
        <input type="radio" id="t-proposal" name="doctype" checked>
        <input type="radio" id="t-vacation" name="doctype">
        <input type="radio" id="t-expense"  name="doctype">
        <input type="radio" id="t-trip"     name="doctype">

        <!-- 탭바 -->
        <div class="tabbar" role="tablist">
          <label for="t-proposal" role="tab" aria-controls="pane-proposal">업무품의서</label>
          <label for="t-vacation" role="tab" aria-controls="pane-vacation">휴가 신청서</label>
          <label for="t-expense"  role="tab" aria-controls="pane-expense">지출 결의서</label>
          <label for="t-trip"     role="tab" aria-controls="pane-trip">출장 보고서</label>
        </div>

        <!-- 폼들 -->
        <div class="forms">
          <!-- 업무품의서 -->
          <section id="pane-proposal" class="form f-proposal">
            <div class="row2"><div>제목</div><input class="input" id="p-title" placeholder="제목을 입력하세요."></div>
            <div style="margin-top:10px">
              <div style="color:var(--muted);font-size:13px;margin-bottom:6px">내용</div>
              <textarea class="textarea" id="p-body" placeholder="내용을 입력하세요."></textarea>
            </div>
            <div style="margin-top:10px;display:flex;gap:8px;align-items:center">
              <input type="file" id="p-file" multiple>
            </div>
          </section>

          <!-- 휴가 신청서 -->
          <section id="pane-vacation" class="form f-vacation">
            <div class="row2"><div>제목</div><input class="input" id="v-title" placeholder="예) 연차 신청"></div>
            <div class="row2" style="margin-top:8px"><div>기간</div>
              <div style="display:flex;gap:8px">
                <input type="date" class="date" id="v-from"><span style="align-self:center">~</span>
                <input type="date" class="date" id="v-to">
              </div>
            </div>
            <div class="row2" style="margin-top:8px">
              <div>종류</div>
              <div style="display:flex;gap:16px;align-items:center">
                <label><input type="radio" name="v-type" value="연차" checked> 연차</label>
                <label><input type="radio" name="v-type" value="반차"> 반차</label>
              </div>
            </div>
            <div style="margin-top:10px">
              <div style="color:var(--muted);font-size:13px;margin-bottom:6px">사유</div>
              <textarea class="textarea" id="v-reason" placeholder="사유를 입력하세요."></textarea>
            </div>
          </section>

          <!-- 지출 결의서 -->
          <section id="pane-expense" class="form f-expense">
            <div class="row2"><div>제목</div><input class="input" id="e-title" placeholder="예) 8월 팀 운영비 결의"></div>
            <div class="row2" style="margin-top:8px"><div>지출 사유</div><input class="input" id="e-reason" placeholder="지출 사유를 입력하세요."></div>

            <div style="margin-top:12px">
              <div style="display:flex;justify-content:space-between;align-items:center">
                <strong>지출 내역</strong>
                <div class="exp-actions">
                  <button type="button" class="btn small" id="btnAddRow">행 추가</button>
                  <button type="button" class="btn small" id="btnDelRow">선택 삭제</button>
                </div>
              </div>
              <table class="exp-table" id="expTable">
                <thead>
                  <tr>
                    <th style="width:40px"><input type="checkbox" id="chkAll"></th>
                    <th style="width:140px">지출일자</th>
                    <th style="width:140px">분류</th>
                    <th>사용처</th>
                    <th style="width:160px">금액</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td class="center"><input type="checkbox" class="row-chk"></td>
                    <td><input type="date" class="date"></td>
                    <td>
                      <select class="select">
                        <option>식대</option><option>교통비</option><option>소모품</option><option>숙박</option><option>기타</option>
                      </select>
                    </td>
                    <td><input class="input" placeholder="사용처 입력"></td>
                    <td><input type="number" class="number money" min="0" step="100" placeholder="0"></td>
                  </tr>
                </tbody>
              </table>
              <div class="sum">합계: <span id="sumMoney">0</span> 원</div>
            </div>
          </section>

          <!-- 출장 보고서 -->
          <section id="pane-trip" class="form f-trip">
            <div class="row2"><div>제목</div><input class="input" id="t-title" placeholder="예) 부산 고객사 미팅 보고"></div>
            <div class="row2" style="margin-top:8px"><div>출장 목적</div><input class="input" id="t-purpose" placeholder="목적 입력"></div>
            <div class="row2" style="margin-top:8px"><div>출장 기간</div>
              <div style="display:flex;gap:8px">
                <input type="date" class="date" id="t-from"><span style="align-self:center">~</span>
                <input type="date" class="date" id="t-to">
              </div>
            </div>
            <div class="row2" style="margin-top:8px"><div>출장 지역</div><input class="input" id="t-area" placeholder="예) 부산 해운대"></div>
            <div style="margin-top:10px">
              <div style="color:var(--muted);font-size:13px;margin-bottom:6px">출장 결과</div>
              <textarea class="textarea" id="t-result" placeholder="결과를 입력하세요."></textarea>
            </div>
          </section>
        </div>
      </div>
    </section>
  </div>

  <!-- 폼 전송용(필요 시) -->
  <form id="draftForm" method="post" enctype="multipart/form-data" style="display:none"></form>
</div>
</body>


<jsp:include page="../footer/footer.jsp" />
