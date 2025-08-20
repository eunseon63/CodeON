<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="../header/header.jsp" />

<style>
:root{
  --bg:#f7f8fb; --card:#fff; --text:#111827; --muted:#6b7280; --line:#e5e7eb; --brand:#2563eb;
}
.hr-wrap{ max-width:1200px; margin:110px auto 60px; color:var(--text); font-family:'Pretendard','맑은 고딕',sans-serif; }
.hr-grid{ display:grid; grid-template-columns: 240px 1fr; gap:24px; }

/* 메인 카드 */
.card{ background:var(--card); border:1px solid var(--line); border-radius:12px; padding:18px; box-shadow:0 2px 10px rgba(0,0,0,.04); }

/* 상단 월 네비 + 필터 */
.topbar{ display:flex; align-items:center; justify-content:space-between; gap:14px; margin-bottom:16px; }
.month-nav{ display:flex; align-items:center; gap:10px; }
.month-btn{ background:#fff; border:1px solid var(--line); border-radius:10px; padding:6px 12px; cursor:pointer; }
.month-current{ font-weight:800; letter-spacing:.02em; }

.filters{ display:flex; align-items:center; gap:10px; }
.select, .input{ border:1px solid var(--line); border-radius:10px; padding:8px 10px; background:#fff; }
.btn{ background:var(--brand); color:#fff; border:0; padding:9px 16px; border-radius:10px; font-weight:700; cursor:pointer; box-shadow:0 2px 8px rgba(37,99,235,.25); }
.btn:hover{ filter:brightness(.96); }
.btn-outline{ background:#fff; color:#111827; border:1px solid var(--line); }

/* 요약 박스 */
.summary{ border:1px solid var(--line); border-radius:12px; padding:16px; margin-bottom:16px; }
.summary h3{ margin:0 0 10px; font-size:15px; color:var(--muted); }
.table-wrap{ border:1px solid var(--line); border-radius:12px; overflow:hidden; }
table{ width:100%; border-collapse:collapse; background:#fff; }
th, td{ padding:12px 14px; border-bottom:1px solid var(--line); text-align:center; font-size:14px; }
th{ background:#f9fafb; font-weight:700; color:#374151; }
tbody tr:last-child td{ border-bottom:none; }

/* 하단 액션/페이지네이션 */
.actions{ display:flex; align-items:center; justify-content:space-between; margin-top:14px; }
.pagination{ display:flex; gap:6px; }
.page-btn{ border:1px solid var(--line); background:#fff; padding:6px 10px; border-radius:8px; text-decoration:none; color:#111827; }
.page-btn.active{ background:#eef2ff; border-color:#c7d2fe; font-weight:700; }

@media (max-width: 980px){
  .hr-grid{ grid-template-columns:1fr; }
  .topbar{ flex-direction:column; align-items:flex-start; }
}
</style>

<div class="hr-wrap">

  <div class="hr-grid">
  <aside class="sidebar-cell">
      <jsp:include page="../admin/adminsidebar.jsp" />
  </aside>
    <!-- 우측 메인 -->
    <section class="card">

      <!-- 상단: 월 네비 + 필터 -->
		<div class="topbar">
		  <!-- 월 네비게이션: prev / next URL을 안전하게 생성 -->
		  <c:url var="prevUrl" value="/hr/attendance">
		    <c:param name="month" value="${prevMonth}"/>
		    <c:if test="${not empty selectedDept}">
		      <c:param name="dept" value="${selectedDept}"/>
		    </c:if>
		    <c:if test="${not empty selectedGrade}">
		      <c:param name="grade" value="${selectedGrade}"/>
		    </c:if>
		  </c:url>
		
		  <c:url var="nextUrl" value="/hr/attendance">
		    <c:param name="month" value="${nextMonth}"/>
		    <c:if test="${not empty selectedDept}">
		      <c:param name="dept" value="${selectedDept}"/>
		    </c:if>
		    <c:if test="${not empty selectedGrade}">
		      <c:param name="grade" value="${selectedGrade}"/>
		    </c:if>
		  </c:url>
		
		  <div class="month-nav">
		    <a class="month-btn" href="${prevUrl}">&lt;-</a>
		    <span class="month-current">${currentMonth}</span>
		    <a class="month-btn" href="${nextUrl}">-&gt;</a>
		  </div>
		
		  <!-- 필터 폼 -->
		  <form class="filters" action="<c:url value='/hr/attendance'/>" method="get">
		    <input type="hidden" name="month" value="${currentMonth}"/>
		
		    <!-- 부서 -->
		    <select name="dept" class="select" title="부서선택">
		      <option value="">부서선택</option>
		      <c:forEach var="d" items="${departments}">
		        <option value="${d.departmentSeq}"
		          <c:if test="${selectedDept eq d.departmentSeq}">selected</c:if>>
		          ${d.departmentName}
		        </option>
		      </c:forEach>
		    </select>
		
		    <!-- 직급 -->
		    <select name="grade" class="select" title="직급선택">
		      <option value="">직급선택</option>
		      <c:forEach var="g" items="${grades}">
		        <option value="${g.gradeSeq}"
		          <c:if test="${selectedGrade eq g.gradeSeq}">selected</c:if>>
		          ${g.gradeName}
		        </option>
		      </c:forEach>
		    </select>
		
		    <button type="submit" class="btn">검색</button>
		  </form>
		</div>


      <!-- 요약(옵션) -->
      <div class="summary">
        <h3>근무 시간 확인</h3>
        <!-- 필요 시 월간 합계, 인원수, 평균 근무시간 등 넣을 자리 -->
      </div>

      <!-- 테이블 -->
      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>근무일</th>
              <th>사원명</th>
              <th>사번</th>
              <th>출근 시각</th>
              <th>퇴근 시각</th>
              <th>근무 시간</th>
              <th>사용연차</th>
              <th>잔여연차</th>
              <th>연장근무</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty rows}">
                <tr><td colspan="9" style="text-align:center; color:var(--muted);">데이터가 없습니다.</td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="r" items="${rows}">
                  <tr>
                    <td>${r.workDateStr}</td>
                    <td>${r.memberName}</td>
                    <td>${r.memberSeq}</td>
                    <td><c:out value="${empty r.startTimeStr ? '-' : r.startTimeStr}"/></td>
                    <td><c:out value="${empty r.endTimeStr   ? '-' : r.endTimeStr}"/></td>
                    <td><c:out value="${empty r.workedTimeStr ? '-' : r.workedTimeStr}"/></td>
                    <td><c:out value="${empty r.usedLeaveStr  ? '-' : r.usedLeaveStr}"/></td>
                    <td><c:out value="${empty r.remainLeaveStr? '-' : r.remainLeaveStr}"/></td>
                    <td><c:out value="${empty r.overtimeStr   ? '-' : r.overtimeStr}"/></td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>

      <!-- 하단: 엑셀 다운로드 + 페이지네이션 -->
      <div class="actions">
        <form action="${ctxPath}/hr/attendance/export" method="get">
          <input type="hidden" name="month" value="${currentMonth}"/>
          <c:if test="${not empty selectedDept}">
            <input type="hidden" name="dept" value="${selectedDept}"/>
          </c:if>
          <c:if test="${not empty selectedGrade}">
            <input type="hidden" name="grade" value="${selectedGrade}"/>
          </c:if>
          <button type="submit" class="btn">엑셀로 다운</button>
        </form>

        <div class="pagination"><!-- 서버에서 page, totalPages 제공 시 사용 -->
          <c:if test="${totalPages > 1}">
            <c:forEach var="p" begin="1" end="${totalPages}">
              <a class="page-btn ${p == page ? 'active' : ''}"
                 href="${ctxPath}/hr/attendance?month=${currentMonth}
                 <c:if test='${not empty selectedDept}'> &amp;dept=${selectedDept}</c:if>
                 <c:if test='${not empty selectedGrade}'> &amp;grade=${selectedGrade}</c:if>
                 &amp;page=${p}">
                 ${p}
              </a>
            </c:forEach>
          </c:if>
        </div>
      </div>

    </section>
  </div>
</div>

<jsp:include page="../footer/footer.jsp" />
