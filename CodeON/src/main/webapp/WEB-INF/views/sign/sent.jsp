<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="../header/header.jsp"/>
<jsp:include page="signsidebar.jsp"/>

<style>
  /* 테이블/배지/링크 공통 스타일 */
  .doc-table { width:100%; border-collapse:collapse; }
  .doc-th, .doc-td { border:1px solid #e5e7eb; padding:8px; }
  .doc-th { background:#f9fafb; font-weight:700; }
  .badge { padding:2px 8px; border-radius:999px; font-size:12px; white-space:nowrap; }
  .b-state-progress { background:#eef2ff; color:#3730a3; } /* 진행중 */
  .b-state-approved { background:#ecfdf5; color:#065f46; } /* 승인 */
  .b-state-rejected { background:#fef2f2; color:#991b1b; } /* 반려 */
  .b-type { background:#f3f4f6; color:#374151; }
  .b-emg { background:#fff5f5; color:#b91c1c; border:1px solid #fecaca; }
  a, a:visited, a:hover, a:active { text-decoration:none; color:inherit; }
</style>

<div class="main-content" style="margin-left:220px;padding:20px 28px 64px;background:#f6f7fb;min-height:100vh">
  <div class="page-wrap" style="max-width:1200px;margin:24px auto">
    <h2 style="margin:0 0 14px;font-weight:800">문서함</h2>

    <div class="card" style="background:#fff;border:1px solid #e5e7eb;border-radius:16px">
      <div class="card-b" style="padding:14px">
        <table class="doc-table">
          <thead>
            <tr>
              <th class="doc-th" style="width:90px">문서번호</th>
              <th class="doc-th" style="width:120px">유형</th>
              <th class="doc-th">제목</th>
              <th class="doc-th" style="width:120px">기안일</th>
              <th class="doc-th" style="width:80px">긴급</th>
              <th class="doc-th" style="width:120px">상태</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty rows}">
                <tr>
                  <td class="doc-td" colspan="6" style="text-align:center;color:#6b7280">
                    상신한 문서가 없습니다.
                  </td>
                </tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="r" items="${rows}">
                  <tr>
                    <td class="doc-td" style="text-align:center">${r.draftSeq}</td>

                    <!-- 유형: 문자열 또는 배지 -->
                    <td class="doc-td" style="text-align:center">
                      <span class="badge b-type"><c:out value="${r.docType}"/></span>
                    </td>

                    <!-- 제목: 상세로 링크 -->
                    <td class="doc-td">
                      <a href="${pageContext.request.contextPath}/sign/view/${r.draftSeq}" style="color:#111;font-weight:600">
                        <c:out value="${r.title}"/>
                      </a>
                    </td>

                    <!-- 기안일 -->
                    <td class="doc-td" style="text-align:center">
                      <fmt:formatDate value="${r.regdate}" pattern="yyyy-MM-dd"/>
                    </td>

                    <!-- 긴급 -->
                    <td class="doc-td" style="text-align:center">
                      <c:if test="${r.isEmergency == 1}">
                        <span class="badge b-emg">긴급</span>
                      </c:if>
                    </td>

                    <!-- 상태: 0/1/9 → 진행중/승인/반려 -->
                    <td class="doc-td" style="text-align:center">
                      <c:choose>
                        <c:when test="${r.status == 1}">
                          <span class="badge b-state-approved">승인</span>
                        </c:when>
                        <c:when test="${r.status == 9}">
                          <span class="badge b-state-rejected">반려</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge b-state-progress">진행중</span>
                        </c:otherwise>
                      </c:choose>
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

<jsp:include page="../footer/footer.jsp"/>
