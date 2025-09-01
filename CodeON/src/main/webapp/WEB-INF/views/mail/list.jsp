<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 부트스트랩 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- 부트스트랩 JS 번들 -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- 부트스트랩 아이콘 CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

<jsp:include page="../header/header.jsp" />
<jsp:include page="mailsidebar.jsp" />

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script type="text/javascript">
$(function () {

    // 안읽은 숫자 표시
    function updateMailCount() {
        $.ajax({
            url: "<%= ctxPath%>/mail/getCount",
            dataType: "json",
            success: function(json) {
                var count = json.count;
                var totalCount = json.totalCount;
                var v_html = count + " / " + totalCount;
                $('span#count').html(v_html);
            },
            error: function(request, status, error){
                alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
            }
        });
    }

    updateMailCount();

    // 글자색 업데이트
    function updateRowReadStatus(row, newStatus) {
        var colorClass = newStatus == 1 ? 'text-secondary' : 'text-dark';
        row.find('td').not(':first').not(':nth-child(2)').removeClass('text-secondary text-dark').addClass(colorClass);
    }

    // 메일 상세 페이지로 이동 (읽음 상태 업데이트 포함)
    $(document).on('click', 'tr.mail-row', function() {
        var row = $(this);
        var emailSeq = row.data('emailseq'); 
        var icon = row.find('.read-icon'); 
        var currentStatus = icon.data('emailreadstatus');
        
        if (currentStatus == 0) {
            $.ajax({
                url: "<%= ctxPath%>/mail/updateReadStatus",
                type: 'POST',
                dataType: "json",
                data: { emailSeq: emailSeq, emailReadStatus: 1 },
                async: false, 
                success: function(json) {
                    if (json.n == 1) {
                        icon.removeClass('bi-envelope-fill text-primary')
                            .addClass('bi-envelope-open-fill text-secondary');
                        icon.data('emailreadstatus', 1);
                        updateRowReadStatus(row, 1);
                        updateMailCount();
                    }
                },
                error: function(request, status, error){
                    console.error("읽음 상태 업데이트 실패", request.responseText);
                }
            });
        }

        window.location.href = '<%= ctxPath%>/mail/view?emailSeq=' + emailSeq;
    });

    // 중요 아이콘 클릭
    $(document).on('click', '.important-icon', function(event) {
        event.stopPropagation();
        var icon = $(this);
        var emailSeq = icon.data('emailseq');
        var currentStatus = icon.data('emailsendimportant');
        var newStatus = currentStatus == 1 ? 0 : 1;

        $.ajax({
            url: "<%= ctxPath%>/mail/updateImportant",
            type: 'POST',
            dataType: "json",
            data: { emailSeq: emailSeq, emailSendImportant: newStatus },
            success: function(json) {
                if (json.n == 1) {
                    if (newStatus == 1) {
                        icon.removeClass('bi-star').addClass('bi-star-fill text-warning');
                    } else {
                        icon.removeClass('bi-star-fill text-warning').addClass('bi-star');
                    }
                    icon.data('emailsendimportant', newStatus);
                } else {
                    alert('중요 표시 변경 실패');
                }
            },
            error: function(request, status, error){
                alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
            }
        });
    });

    // 읽음 상태 아이콘 클릭
    $(document).on('click', '.read-icon', function(event) {
        event.stopPropagation();
        var icon = $(this);
        var row = icon.closest('tr');
        var emailSeq = icon.data('emailseq');
        var currentStatus = icon.data('emailreadstatus');
        var newStatus = currentStatus == 1 ? 0 : 1;

        $.ajax({
            url: "<%= ctxPath%>/mail/updateReadStatus",
            type: 'POST',
            dataType: "json",
            data: { emailSeq: emailSeq, emailReadStatus: newStatus },
            success: function(json) {
                if (json.n == 1) {
                    if (newStatus == 1) {
                        icon.removeClass('bi-envelope-fill text-primary')
                            .addClass('bi-envelope-open-fill text-secondary');
                    } else {
                        icon.removeClass('bi-envelope-open-fill text-secondary')
                            .addClass('bi-envelope-fill text-primary');
                    }
                    icon.data('emailreadstatus', newStatus);
                    updateRowReadStatus(row, newStatus);
                    updateMailCount();
                } else {
                    alert('읽음 상태 변경 실패');
                }
            },
            error: function(request, status, error){
                alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
            }
        });
    });

    // 첨부파일 아이콘 클릭 시 이벤트 전파 차단
    $(document).on("click", ".attach-icon", function(event) {
        event.stopPropagation();
    });

    // 체크박스 클릭 시 이벤트 전파 차단
    $(document).on("click", "input[name='chkMail']", function(event) {
        event.stopPropagation();
    });

    // 전체 선택/해제
    $(document).on("change", "#chkAll", function() {
        $("input[name='chkMail']").prop("checked", this.checked);
    });

    // 선택 메일 삭제
    $(document).on("click", "#btnDelete", function() {
        let checkedMails = $("input[name='chkMail']:checked");
        if (checkedMails.length === 0) {
            alert("삭제할 메일을 선택하세요.");
            return;
        }

        if (!confirm("선택한 메일을 삭제하시겠습니까?")) return;

        let emailSeqArr = [];
        checkedMails.each(function() {
            emailSeqArr.push($(this).val());
        });

        $.ajax({
            url: "<%= ctxPath%>/mail/deleteMails",
            type: "POST",
            traditional: true,
            data: { emailSeqList: emailSeqArr },
            dataType: "json",
            success: function(json) {
                if (json.n > 0) {
                    alert("메일이 삭제되었습니다.");
                    location.reload();
                } else {
                    alert("메일 삭제 실패");
                }
            },
            error: function(request, status, error){
                alert("code: " + request.status + "\n" + "message: " + request.responseText + "\n" + "error: " + error);
            }
        });
    });

});
</script>

<main style="margin-left: 240px; padding-top: 20px;">
    <div class="container-fluid">
        <h3 class="mb-4">전체 메일함 <span id="count"></span></h3> 

        <!-- 검색 카드 -->
        <div class="card mb-4 shadow-sm">
            <div class="card-body row g-2 align-items-center">
                <div class="col-md-8 col-sm-12">
                    <input type="text" id="searchWord" class="form-control" placeholder="메일 검색..." value="${param.searchWord}">
                </div>
                <div class="col-md-4 col-sm-12 text-md-end mt-2 mt-md-0">
                    <button class="btn btn-primary" id="btnSearch">검색</button>
                    <button class="btn btn-danger ms-2" id="btnDelete">삭제</button>
                </div>
            </div>
        </div>

        <!-- 메일 테이블 카드 -->
        <div class="card shadow-sm">
            <div class="card-body p-0">
                <table class="table table-hover mb-0">
                    <thead class="table-light">
                        <tr>
                            <th scope="col" style="width:40px;">
                                <input type="checkbox" id="chkAll">
                            </th>
                            <th scope="col" style="width:80px;"></th>
                            <th scope="col">보낸 사람</th>
                            <th scope="col">제목</th>
                            <th scope="col">날짜</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mail" items="${mailList}">
                            <tr class="mail-row" style="cursor:pointer;" data-emailseq="${mail.emailSeq}">
                                <td class="align-middle text-center">
                                    <input type="checkbox" name="chkMail" value="${mail.emailSeq}">
                                </td>
                                <td class="align-middle text-center">
                                    <div class="d-flex justify-content-center align-items-center gap-2" style="font-size: 1.25rem; cursor: pointer;">
                                        <i class="bi ${mail.emailSendImportant == 1 ? 'bi-star-fill text-warning' : 'bi-star'} important-icon"
                                           data-emailseq="${mail.emailSeq}"
                                           data-emailsendimportant="${mail.emailSendImportant}"
                                           title="중요"></i>
                                        <i class="bi ${mail.emailReadStatus == 1 ? 'bi-envelope-open-fill text-secondary' : 'bi-envelope-fill text-primary'} read-icon"
                                           data-emailseq="${mail.emailSeq}"
                                           data-emailreadstatus="${mail.emailReadStatus}"
                                           title="메일 상태"></i>
                                        <c:if test="${not empty mail.emailFilename}">
                                            <i class="bi bi-paperclip attach-icon" title="첨부파일"></i>
                                        </c:if>
                                    </div>
                                </td>
                                <td class="align-middle ${mail.emailReadStatus == 1 ? 'text-secondary' : 'text-dark'}">${mail.sendMemberEmail}</td>
                                <td class="align-middle ${mail.emailReadStatus == 1 ? 'text-secondary' : 'text-dark'}">${mail.emailTitle}</td>
                                <td class="align-middle ${mail.emailReadStatus == 1 ? 'text-secondary' : 'text-dark'}">${mail.emailRegdate}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- 페이지바 -->
                <div align="center" style="border: solid 0px gray; width: 80%; margin: 30px auto;">
                    ${requestScope.pageBar}
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="../footer/footer.jsp" />
