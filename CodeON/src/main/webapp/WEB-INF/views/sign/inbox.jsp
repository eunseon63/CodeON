<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="../header/header.jsp"/>
<jsp:include page="signsidebar.jsp"/>

<div class="main-content" style="margin-left:220px;padding:20px 28px 64px;background:#f6f7fb;min-height:100vh">
  <div class="page-wrap" style="max-width:1200px;margin:24px auto">
    <h2 style="margin:0 0 14px;font-weight:800">결재하기</h2>

    <div class="card" style="background:#fff;border:1px solid #e5e7eb;border-radius:16px">
      <div class="card-b" style="padding:14px">
        <table style="width:100%;border-collapse:collapse">
          <thead>
            <tr style="background:#f9fafb">
              <th style="border:1px solid #e5e7eb;padding:8px;width:90px">문서번호</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:100px">유형</th>
              <th style="border:1px solid #e5e7eb;padding:8px">제목</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:120px">기안자</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:120px">기안일</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:80px">긴급</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:130px">처리</th>
            </tr>
          </thead>
          <tbody>
          <c:choose>
            <c:when test="${empty rows}">
              <tr><td colspan="7" style="border:1px solid #e5e7eb;padding:16px;text-align:center;color:#6b7280">대기 중인 결재가 없습니다.</td></tr>
            </c:when>
            <c:otherwise>
              <c:forEach var="r" items="${rows}">
                <tr>
                  <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">${r.draftSeq}</td>
                  <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">${r.docType}</td>
                  <td style="border:1px solid #e5e7eb;padding:8px">
                    <a href="${pageContext.request.contextPath}/sign/view/${r.draftSeq}" style="text-decoration:none;color:#111">${r.title}</a>
                  </td>
                  <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">${r.drafterName}</td>
                  <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">
                    <fmt:formatDate value="${r.regdate}" pattern="yyyy-MM-dd"/>
                  </td>
                  <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">
                    <c:if test="${r.isEmergency==1}">✅</c:if>
                  </td>
                  <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">
                    <button class="btn" onclick="approve(${r.draftLineSeq})" style="height:32px;border-radius:8px;border:1px solid #e5e7eb">승인</button>
                    <button class="btn" onclick="reject(${r.draftLineSeq})"  style="height:32px;border-radius:8px;border:1px solid #e5e7eb">반려</button>
                  </td>
                </tr>
              </c:forEach>
            </c:otherwise>
          </c:choose>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script>
const ctx='${pageContext.request.contextPath}';
async function approve(id){
  const comment = prompt('의견(선택):')??'';
  const res = await fetch(`${ctx}/sign/lines/${id}/approve`, {method:'POST', body:new URLSearchParams({comment})});
  const j = await res.json(); if(!j.ok){ alert(j.msg||'실패'); } location.reload();
}
async function reject(id){
  const comment = prompt('반려 사유(필수):');
  if(!comment || !comment.trim()){ alert('반려 사유를 입력하세요.'); return; }
  const res = await fetch(`${ctx}/sign/lines/${id}/reject`, {method:'POST', body:new URLSearchParams({comment})});
  const j = await res.json(); if(!j.ok){ alert(j.msg||'실패'); } location.reload();
}
</script>

<jsp:include page="../footer/footer.jsp"/>
