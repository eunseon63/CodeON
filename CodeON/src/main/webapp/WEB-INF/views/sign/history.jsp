<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="../header/header.jsp"/>
<jsp:include page="signsidebar.jsp"/>

<div class="main-content" style="margin-left:220px;padding:20px 28px 64px;background:#f6f7fb;min-height:100vh">
  <div class="page-wrap" style="max-width:1200px;margin:24px auto">
    <h2 style="margin:0 0 14px;font-weight:800">결재함</h2>

    <div class="card" style="background:#fff;border:1px solid #e5e7eb;border-radius:16px">
      <div class="card-b" style="padding:14px">
        <table style="width:100%;border-collapse:collapse">
          <thead>
            <tr style="background:#f9fafb">
              <th style="border:1px solid #e5e7eb;padding:8px;width:90px">문서번호</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:100px">유형</th>
              <th style="border:1px solid #e5e7eb;padding:8px">제목</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:120px">기안자</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:120px">처리일시</th>
              <th style="border:1px solid #e5e7eb;padding:8px;width:100px">내 결재</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty rows}">
                <tr><td colspan="6" style="border:1px solid #e5e7eb;padding:16px;text-align:center;color:#6b7280">처리한 문서가 없습니다.</td></tr>
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
                      <fmt:formatDate value="${r.signDate}" pattern="yyyy-MM-dd"/>
                    </td>
                    <td style="border:1px solid #e5e7eb;padding:8px;text-align:center">
                      <c:choose>
                        <c:when test="${r.myStatus==1}"><span style="color:#16a34a">승인</span></c:when>
                        <c:when test="${r.myStatus==9}"><span style="color:#ef4444">반려</span></c:when>
                        <c:otherwise>-</c:otherwise>
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

<jsp:include page="../footer/footer.jsp"/>
