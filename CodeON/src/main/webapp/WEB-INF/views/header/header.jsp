<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  String ctxPath = request.getContextPath();
%>

<style>
/* ===============================
   CODEON Header — Clean Minimal
   =============================== */
:root{
  --hdr-h: 68px;
  --bg: #f5f7fb;
  --surface: #ffffff;
  --text: #0f172a;
  --muted: #64748b;
  --line: #e6e8ed;
  --brand: #2563eb;
  --brand-strong:#1e40af;
  --shadow: 0 10px 30px rgba(15,23,42,.06);
  --radius: 14px;
}

/* 페이지 상단 여백(고정 헤더 높이만큼) */
body{ padding-top: var(--hdr-h); background: var(--bg); }

/* 헤더 */
.site-header{
  position: fixed; inset: 0 0 auto 0; height: var(--hdr-h);
  background: var(--surface); border-bottom:1px solid var(--line);
  display:flex; align-items:center; justify-content:center; z-index: 1000;
  box-shadow: var(--shadow);
}
.hdr-inner{
  width: 100%; max-width: 1240px; padding: 0 20px;
  display:flex; align-items:center; justify-content:space-between; gap:16px;
}

/* 좌측: 로고 + 내비 */
.hdr-left{ display:flex; align-items:center; gap: 22px; min-width: 0; }
.logo{
  display:flex; align-items:center; gap:10px; text-decoration:none;
  font-weight:900; color:var(--text); font-size:20px;
}
.logo img{ height: 40px; display:block; }

/* 내비게이션 */
.nav{
  display:flex; align-items:center; gap: 16px; overflow-x:auto; scrollbar-width:none;
}
.nav::-webkit-scrollbar{ display:none; }
.nav a{
  position:relative; display:inline-flex; align-items:center;
  height: 34px; padding: 0 10px; border-radius:10px;
  color: #0f172a; text-decoration:none; font-weight:700; white-space:nowrap;
  transition: background .18s ease, color .18s ease;
}
.nav a:hover{ background: #eef2ff; color: var(--brand-strong); }
.nav a.active{ color: var(--brand-strong); background:#eef2ff; }

/* 우측: 로그아웃 */
.logout-btn{
  appearance:none; border:1px solid transparent; border-radius: 10px;
  background: linear-gradient(180deg,#3b82f6,#2563eb); color:#fff;
  font-weight:800; padding:8px 14px; cursor:pointer;
  box-shadow: 0 8px 22px rgba(37,99,235,.25);
  transition: transform .08s ease, filter .2s ease;
}
.logout-btn:hover{ filter: brightness(.97) }
.logout-btn:active{ transform: translateY(1px) }

/* 토스트(알림 배너) 컨테이너는 JS에서 주입) */
</style>

<script>
// 헤더 로드 시 WebSocket 연결 + 알림 토스트
(function(){
  if (window.__APP_WS__) return; // 중복 연결 방지

  var ctx = '<%= ctxPath %>';
  var wsUrl = (location.protocol === 'https:' ? 'wss://' : 'ws://') + location.host + ctx + '/chatting/multichatstart';

  try {
    var ws = new WebSocket(wsUrl);
    window.__APP_WS__ = ws;
  } catch (e) {
    console.error('WebSocket open failed:', e);
    return;
  }

  ws.onmessage = function(ev){
    var txt = ev.data || '';
    if (txt.trim().startsWith('{')) {
      try {
        var d = JSON.parse(txt);
        if (d.kind === 'notify') {
          // ▶ 탭 기준 10초 중복 차단
          if (d.notiId) {
            var key = 'seen_noti_' + d.notiId;
            var now = Date.now();
            var rec = sessionStorage.getItem(key);
            if (rec && (now - parseInt(rec, 10) < 10000)) return;
            sessionStorage.setItem(key, String(now));
          }
          showToast(d.title || '알림', d.body || '', d.link || '#');
          return;
        }
      } catch(e){ /* fallthrough */ }
    }
  };

  function showToast(title, body, link){
    var wrap = document.getElementById('toast-wrap');
    if (!wrap) {
      wrap = document.createElement('div');
      wrap.id = 'toast-wrap';
      wrap.style.position = 'fixed';
      wrap.style.top = '16px';
      wrap.style.right = '16px';
      wrap.style.zIndex = '2000';
      wrap.style.display = 'flex';
      wrap.style.flexDirection = 'column';
      wrap.style.gap = '10px';
      document.body.appendChild(wrap);
    }

    var card = document.createElement('div');
    card.style.minWidth = '280px';
    card.style.maxWidth = '360px';
    card.style.padding = '12px 14px';
    card.style.borderRadius = '12px';
    card.style.boxShadow = '0 12px 26px rgba(15,23,42,.28)';
    card.style.background = '#0f172a';
    card.style.color = '#fff';
    card.style.cursor = link ? 'pointer' : 'default';
    card.style.border = '1px solid rgba(255,255,255,.08)';
    card.innerHTML =
      '<div style="font-weight:800; letter-spacing:.2px">'+ (title||'알림') +'</div>' +
      '<div style="font-size:13px;margin-top:4px;opacity:.9">'+ (body||'') +'</div>';

    if (link) card.addEventListener('click', function(){ location.href = link; });
    wrap.appendChild(card);

    setTimeout(function(){
      card.style.transition = 'opacity .35s, transform .35s';
      card.style.opacity = '0';
      card.style.transform = 'translateY(-6px)';
      setTimeout(function(){ card.remove(); }, 360);
    }, 4500);
  }
})();
</script>

<header class="site-header">
  <div class="hdr-inner">
    <div class="hdr-left">
      <a href="<%= ctxPath %>/index" class="logo" aria-label="CODEON 홈">
        <img src="<%= ctxPath %>/image/logo.png" alt="CODEON" />
      </a>
      <nav class="nav">
        <a href="<%= ctxPath %>/board/list">게시판</a>
        <a href="<%= ctxPath %>/mail/list">메일</a>
        <a href="<%= ctxPath %>/schedule/scheduleManagement">일정</a>
        <a href="<%= ctxPath %>/member/work">근태관리</a>
        <a href="<%= ctxPath %>/sign/main">전자결재</a>
        <a href="<%= ctxPath %>/address">주소록</a>
        <a href="<%= ctxPath %>/mypage">마이페이지</a>
        <a href="<%= ctxPath %>/chatting/multichat">웹채팅</a>
        <a href="<%= ctxPath %>/survey/main">설문</a>
        <a href="<%= ctxPath %>/member/register">인사(인사팀만)</a>
      </nav>
    </div>

    <form action="<%= ctxPath %>/login/logout" method="get">
      <button type="submit" class="logout-btn">로그아웃</button>
    </form>
  </div>
</header>
