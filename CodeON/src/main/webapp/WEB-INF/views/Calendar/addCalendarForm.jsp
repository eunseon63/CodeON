<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    String ctxPath  = request.getContextPath();
    String role     = (String) session.getAttribute("role");     // "ADMIN", "TEAM_LEADER", ...
    String loginId  = (String) session.getAttribute("loginId");
%>

<html>
<head>
<jsp:include page="/WEB-INF/views/header/header.jsp" />

<meta charset="UTF-8">
<title>일정 등록</title>

<style>
:root{
  --bg:#f4f6f8; --card:#ffffff; --line:#e5e7eb; --text:#111827; --muted:#6b7280; --brand:#22c55e;
  --radius:12px; --shadow:0 8px 20px rgba(0,0,0,.08);
}
*{box-sizing:border-box}
body{margin:0; background:var(--bg); color:var(--text); font-family:-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Noto Sans KR",Arial,Helvetica,sans-serif;}
.container{max-width:760px; margin:46px auto; background:var(--card); border:1px solid var(--line); border-radius:16px; box-shadow:var(--shadow); padding:28px 34px;}
h2{margin:0 0 18px; text-align:center}
label{font-weight:600; color:#374151; margin-top:14px; display:block}
input, textarea, select{width:100%; padding:10px 12px; margin-top:6px; border:1px solid #cfd4dc; border-radius:10px; font-size:14px; background:#fafafa; transition:all .2s}
input:focus, textarea:focus, select:focus{outline:none; border-color:#8b5cf6; background:#fff; box-shadow:0 0 0 3px rgba(139,92,246,.15)}
textarea{resize:vertical; min-height:110px}
.row{display:grid; grid-template-columns:1fr 1fr; gap:14px}
.helper{font-size:12px; color:var(--muted); margin-top:6px}
.inline{display:flex; gap:10px; align-items:center}
.btn-submit{margin-top:22px; width:100%; padding:12px; background:linear-gradient(90deg,#22c55e,#16a34a); border:none; border-radius:10px; color:#fff; font-size:16px; font-weight:700; cursor:pointer}
.btn-submit:hover{filter:brightness(.98)}
.hidden{display:none}
</style>

<script>
// === 타입 ↔ 대분류 텍스트 매핑 ===
const TYPE_TO_BIG_TEXT = {
  COMPANY:    '사내',
  DEPARTMENT: '부서',
  PERSONAL:   '개인',
  SHARE:      '공유'
};

// 대분류 <select>에서 라벨 텍스트로 선택
function selectBigByText(keyword){
  const sel = document.getElementById('bigCategorySeq');
  const kw  = (keyword||'').toLowerCase();
  for(const opt of sel.options){
    const txt = (opt.textContent||opt.innerText||'').toLowerCase();
    if(txt.includes(kw)){ sel.value = opt.value; return true; }
  }
  return false;
}

// 소분류: data-fk로 대분류 필터링
function filterSmallByBig(){
  const bigVal = document.getElementById('bigCategorySeq').value;
  const smSel  = document.getElementById('smallCategorySeq');
  const opts   = smSel.querySelectorAll('option[data-fk]');
  smSel.value = ''; // 초기화
  opts.forEach(o=>{
    const match = !bigVal || o.getAttribute('data-fk') === bigVal;
    o.style.display = match ? '' : 'none';
  });
}

// 타입 변경 시: 대분류 자동맞춤, 공유필드 토글, 대분류 잠금
function onCalendarTypeChange(){
  const type = document.getElementById('calendarType').value;
  const want = TYPE_TO_BIG_TEXT[type];
  if (want){
    selectBigByText(want);
    filterSmallByBig();
    // 공유 입력 토글
    document.getElementById('shareEmployeesGroup').classList.toggle('hidden', type!=='SHARE');
    // 대분류 잠금: SHARE만 직접 고를 수 있게
    document.getElementById('bigCategorySeq').disabled = (type!=='SHARE');
  }
  // 타입 기본색 가이드(선택): DB에서 색을 관리한다면 주석 가능
  const colorInput = document.getElementById('calendarColor');
  if(!colorInput.value){
    const def = defaultColorByType(type);
    if(def) colorInput.value = def;
  }
}

// 타입별 기본색(미입력/플레이스홀더일 때만 힌트용)
function defaultColorByType(type){
  switch(type){
    case 'COMPANY':    return '#6b46c1';
    case 'DEPARTMENT': return '#2563eb';
    case 'PERSONAL':   return '#16a34a';
    case 'SHARE':      return '#f59e0b';
    default: return '';
  }
}

// 제출 전 최종 검증 (타입/대분류 일치, 공유 입력, 시간 범위 등)
function validateForm(){
  const title = document.getElementById('title').value.trim();
  const start = document.getElementById('startDate').value;
  const end   = document.getElementById('endDate').value;
  const type  = document.getElementById('calendarType').value;
  const bigSel= document.getElementById('bigCategorySeq');
  const bigTxt= (bigSel.options[bigSel.selectedIndex]?.textContent || '').trim();

  if(!title){ alert('제목을 입력하세요.'); return false; }
  if(!start || !end){ alert('시작/종료 일시를 모두 입력하세요.'); return false; }
  if(new Date(start) > new Date(end)){ alert('종료일은 시작일 이후여야 합니다.'); return false; }

  const want = TYPE_TO_BIG_TEXT[type] || '';
  if(!want || !bigTxt.includes(want)){
    alert(`선택한 "캘린더 선택"(${type})과 "대분류"가 일치해야 합니다. (예: ${want})`);
    return false;
  }

  if(type==='SHARE'){
    const share = document.getElementById('shareEmployees').value.trim();
    if(!share){ alert('공유 캘린더는 공유할 직원을 입력하세요.'); return false; }
  }
  return true;
}

document.addEventListener('DOMContentLoaded', ()=>{
  // 초기 진입: 타입 기본값 결정 (권한에 따라)
  const typeSel = document.getElementById('calendarType');
  if(!typeSel.value){ typeSel.value = 'PERSONAL'; }
  onCalendarTypeChange();

  // 이벤트 바인딩
  typeSel.addEventListener('change', onCalendarTypeChange);
  document.getElementById('bigCategorySeq').addEventListener('change', filterSmallByBig);

  // 초기 소분류 필터
  filterSmallByBig();
});
</script>
</head>

<body>
<div class="container">
  <h2>일정 등록</h2>

  <form action="<%= ctxPath %>/Calendar/addCalendarForm" method="post" onsubmit="return validateForm();">
    <!-- 캘린더 선택 (권한별 노출) -->
    <label>캘린더 선택</label>
    <select name="calendarType" id="calendarType" required>
      <c:if test="${role == 'ADMIN'}">
        <option value="COMPANY">사내 캘린더</option>
      </c:if>
      <c:if test="${role == 'TEAM_LEADER'}">
        <option value="DEPARTMENT">부서 캘린더</option>
      </c:if>
      <option value="PERSONAL">내 캘린더</option>
      <option value="SHARE">공유 캘린더</option>
    </select>
    <div class="helper">선택한 캘린더에 맞춰 대분류가 자동으로 매칭됩니다.</div>

    <!-- 대분류 / 소분류 -->
    <label>대분류 선택</label>
    <select name="bigCategorySeq" id="bigCategorySeq" required>
      <option value="">-- 선택하세요 --</option>
      <c:forEach var="bigCat" items="${bigCategoryList}">
        <option value="${bigCat.bigCategorySeq}">${bigCat.bigCategoryName}</option>
      </c:forEach>
    </select>

    <label>소분류 선택</label>
    <select name="smallCategorySeq" id="smallCategorySeq">
      <option value="">-- 선택하세요 --</option>
      <c:forEach var="smallCat" items="${smallCategoryList}">
        <!-- fk_big_category_seq를 data-fk로 실어 필터링 -->
        <option value="${smallCat.smallCategorySeq}" data-fk="${smallCat.fkBigCategorySeq}">
          ${smallCat.smallCategoryName}
        </option>
      </c:forEach>
    </select>

    <!-- 제목/내용 -->
    <label>일정 제목</label>
    <input type="text" id="title" name="title" placeholder="일정 제목을 입력하세요" required>

    <label>일정 내용</label>
    <textarea name="content" placeholder="세부 내용을 입력하세요"></textarea>

    <!-- 시작/종료 -->
    <div class="row">
      <div>
        <label>시작 날짜</label>
        <input type="datetime-local" id="startDate" name="startDate" required>
      </div>
      <div>
        <label>종료 날짜</label>
        <input type="datetime-local" id="endDate" name="endDate" required>
      </div>
    </div>

    <!-- 장소 / 색상 -->
    <div class="row">
      <div>
        <label>장소</label>
        <input type="text" id="calendarLocation" name="calendarLocation" placeholder="회의실 A, 온라인 등">
      </div>
      <div>
        <label>색상</label>
        <input type="color" id="calendarColor" name="calendarColor" value="">
        <div class="helper">미선택 시 서버/DB 기본 정책을 따릅니다.</div>
      </div>
    </div>

    <!-- 공유 입력 -->
    <div id="shareEmployeesGroup" class="hidden">
      <label>공유할 직원</label>
      <input type="text" id="shareEmployees" name="shareEmployees" placeholder="사원번호 또는 사원이름, 쉼표(,)로 구분">
      <div class="helper">예: 101, 205, 김철수</div>
    </div>

    <!-- 반복 -->
    <label>반복</label>
    <select name="repeatType">
      <option value="NONE">반복 없음</option>
      <option value="DAILY">매일</option>
      <option value="WEEKLY">매주</option>
      <option value="MONTHLY">매월</option>
    </select>

    <button type="submit" class="btn-submit">등록하기</button>
  </form>
</div>

<jsp:include page="../footer/footer.jsp" />
</body>
</html>
