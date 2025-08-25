<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<jsp:include page="header/header.jsp" />

<style>
:root{
  --bg:#f7f8fb;
  --card:#ffffff;
  --text:#111827;
  --muted:#6b7280;
  --line:#e5e7eb;
  --brand:#2563eb;
  --brand-2:#1d4ed8;
}

/* 페이지 레이아웃 */
body{ background:var(--bg); }
.dashboard{
  /* 헤더와 같은 기준선에서 시작하도록 상단 여백 최소화 */
  max-width: 1280px;
  margin: 24px auto 64px;
  padding: 0 16px;
  font-family: 'Pretendard','맑은 고딕', system-ui, -apple-system, sans-serif;
  color: var(--text);
}
.grid-3{
  display: grid;
  grid-template-columns: 320px 1fr 420px; /* 좌/중/우 비율 조정 */
  gap: 24px;              /* 컬럼 간 간격 */
  align-items: start;     /* 세 컬럼 상단 정렬(같은 위치에서 시작) */
}

/* 카드 공통 */
.card{
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
}
.card-hd{
  padding: 12px 16px;
  border-bottom: 1px solid var(--line);
  font-weight: 800;
  letter-spacing: .02em;
}
.card-bd{ padding: 16px; }
.mt-3{ margin-top: 16px; }

/* 프로필 카드 */
.profile{
  display: grid;
  grid-template-columns: 84px 1fr;
  gap: 14px;
  align-items: center;
}
.avatar{
  width: 84px; height: 84px;
  border-radius: 50%;
  border: 3px solid #2d6cdf33;
  display:flex; align-items:center; justify-content:center;
  font-size: 40px; color:#2d6cdf;
  background:#f3f6ff;
}
.profile-name{
  font-weight: 800; font-size: 20px;
}
.profile-meta{
  margin-top: 6px; color: var(--muted); font-size: 12px;
}
.sep{ height:1px; background:var(--line); margin:12px 0; }

/* 출퇴근 박스 */
.work-box{ text-align: left; }
.work-row{ display:flex; align-items:baseline; justify-content:space-between; margin-bottom:8px; }
.work-label{ color:var(--muted); font-size:12px; }
.work-time{ font-size:22px; font-weight:800; letter-spacing:.02em; }
.work-actions{ margin-top:8px; }
.work-btn{
  background: var(--brand);
  color:#fff; border:0; padding:8px 14px;
  border-radius:10px; cursor:pointer; font-weight:700;
  box-shadow:0 2px 8px rgba(37,99,235,.20);
  transition: transform .06s ease, filter .2s ease;
}
.work-btn + .work-btn{ margin-left:6px; }
.work-btn:hover{ filter:brightness(.96); }
.work-btn:active{ transform: translateY(1px); }

/* 리스트(공지/일정) */
.list{ list-style:none; padding:0; margin:0; }
.list li{ padding:12px 0; border-bottom:1px solid var(--line); }
.list li:last-child{ border-bottom:none; }
.item-title{ font-weight:700; }
.item-meta{ color:var(--muted); font-size:12px; }

/* 공지 큰 영역(가운데) */
.notice-area{
  min-height: 420px;      /* 스샷처럼 넉넉한 높이 */
  background: #fff;
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 18px;
}

/* 반응형 */
@media (max-width: 1200px){
  .grid-3{ grid-template-columns: 300px 1fr 360px; }
}
@media (max-width: 980px){
  .grid-3{ grid-template-columns: 1fr; }
}
</style>

<div class="dashboard">
  <div class="grid-3">

    <!-- 좌측 컬럼: 프로필 → 시계/출퇴근 → 날씨 -->
    <aside>
      <!-- 프로필 카드 -->
      <div class="card">
        <div class="card-bd">
          <div class="profile">
            <div class="avatar">👤</div>
            <div>
              <div class="profile-name">
                <!-- 사용자명 바인딩: 필요 시 ${userName} 로 교체 -->
                아무개 <span class="item-meta" style="font-weight:600;">사원님</span>
              </div>
              <div class="profile-meta">출근 시간 → 퇴근 시간</div>
              <div class="profile-meta">08:40:07 &nbsp;&nbsp;→&nbsp;&nbsp; --:--:--</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 시계/출퇴근 카드 -->
      <div class="card mt-3">
        <div class="card-bd">
          <div class="item-meta" style="text-align:center; font-weight:700;">2025-08-06(수)</div>
          <div style="text-align:center; font-size:36px; font-weight:800; letter-spacing:.02em;">08:42:33</div>

          <div class="sep"></div>

          <!-- 네가 준 출퇴근 블록(표현만 유지) -->
          <div class="work-box">
            <div class="work-row">
              <span class="work-label">출근시간</span>
              <span class="work-time">
                <c:set var="startTime" value="-" />
                <c:forEach var="att" items="${attendanceList}">
                  <c:if test="${att.workDateStr eq todayStr}">
                    <c:set var="startTime" value="${att.startTimeStr != null ? att.startTimeStr : '-'}" />
                  </c:if>
                </c:forEach>
                ${startTime}
              </span>
            </div>

            <div class="work-row">
              <span class="work-label">퇴근시간</span>
              <span class="work-time">
                <c:set var="endTime" value="-" />
                <c:forEach var="att" items="${attendanceList}">
                  <c:if test="${att.workDate eq todayStr}">
                    <c:set var="endTime" value="${att.endTimeStr != null ? att.endTimeStr : '-'}" />
                  </c:if>
                </c:forEach>
                ${endTime}
              </span>
            </div>

            <div class="work-actions">
              <form action="${pageContext.request.contextPath}/member/startWork" method="post" style="display:inline;">
                <button type="submit" class="work-btn">출근</button>
              </form>
              <form action="${pageContext.request.contextPath}/member/endWork" method="post" style="display:inline;">
                <button type="submit" class="work-btn" style="background:var(--brand-2);">퇴근</button>
              </form>
            </div>
          </div>
        </div>
      </div>

      <!-- 날씨 카드 -->
      <div class="card mt-3">
        <div class="card-bd" style="display:flex; align-items:center; gap:14px;">
          <div style="font-size:36px;">☀️</div>
          <div>
            <div class="item-title">덥다</div>
            <div style="font-weight:800; font-size:18px;">30도</div>
          </div>
        </div>
      </div>
    </aside>

    <!-- 가운데 컬럼: 공지 영역(큰 박스) -->
    <section>
      <div class="notice-area">
        <div class="item-title" style="margin-bottom:12px;">게시판 중 공지사항 불러오기</div>
        <!-- 공지 내용 또는 리스트 배치 영역 -->
      </div>
    </section>

    <!-- 우측 컬럼: 오늘 일정 -->
    <aside>
      <div class="card">
        <div class="card-bd">
          <div class="item-title" style="margin-bottom:10px;">📌 오늘 예정된 일정</div>
          <ul class="list">
            <li>- 10:00 전산팀 월말회의</li>
            <li>- 13:00 유산균 먹기</li>
            <li>- 19:00 팀 전체 회식</li>
          </ul>
        </div>
      </div>
    </aside>

  </div>
</div>

<jsp:include page="footer/footer.jsp" />
