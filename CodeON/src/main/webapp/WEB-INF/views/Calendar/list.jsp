<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctxPath = request.getContextPath();
%>

<html>
<head>
<jsp:include page="/WEB-INF/views/header/header.jsp" />

<title>일정 캘린더</title>

<link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.css" rel="stylesheet" />
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- 구글 캘린더 플러그인 -->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@fullcalendar/google-calendar@6.1.7/index.global.min.js"></script>

<style>
.container { display: flex; margin-top: 30px; font-family: 'Arial'; }
#calendar-sidebar { width: 250px; padding: 40px; border-right: 1px solid #ccc; margin-top: 40px; }
#calendar { flex: 1; padding: 20px; margin-top: 80px; margin-bottom: 80px; }
#calendar-sidebar button { display: block; width: 100%; padding: 8px 10px; margin-bottom: 10px; font-size: 14px; cursor: pointer; border: 1px solid #888; border-radius: 4px; background-color: #f0f0f0; transition: background-color 0.2s ease; }
#calendar-sidebar button:hover { background-color: #ddd; }
.fc-day-today { background-color: transparent !important; }
</style>
</head>

<body>

<input type="hidden" value="${sessionScope.loginuser.memberSeq}" id="fk_userid" />

<div class="container">
    <div id="calendar-sidebar">
        <button onclick="loadCategory('사내')">사내 일정</button>
        <button onclick="loadCategory('부서')">부서 일정</button>
        <button onclick="loadCategory('개인')">개인 일정</button>
        <button onclick="loadCategory('공유')">공유 일정</button>
        <button onclick="loadCategory('전체')">전체 일정</button>
    </div>

    <div id="calendar"></div>
</div>

<script>
let calendar;
let currentCategory = '전체'; // 기본 카테고리

function initCalendar() {
    const calendarEl = document.getElementById('calendar');

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },

        // 구글 캘린더 대한민국 공휴일 표시
        googleCalendarApiKey: "AIzaSyASM5hq3PTF2dNRmliR_rXpjqNqC-6aPbQ",
        eventSources: [
            {
                googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                color: 'white',
                textColor: 'red'
            }
        ],

        // DB 연동 Ajax 이벤트
        events: function(fetchInfo, successCallback, failureCallback) {
            const userid = document.getElementById('fk_userid').value;

            $.ajax({
                url: '<%= ctxPath %>/Calendar/selectCalendar',
                type: 'GET',
                data: { fk_userid: userid }, // 서버에서 calendar_user 기준
                dataType: 'json',
                success: function(json) {
                    console.log("서버 JSON:", json);
                    const events = [];

                    json.forEach(function(item) {
                        // 카테고리 필터 적용
                        if (currentCategory === '전체' || currentCategory === item.type) {
                            events.push({
                                id: item.id,
                                title: item.title,
                                start: item.start,
                                end: item.end,
                                color: item.color || '#3788d8',
                                url: '<%= ctxPath %>/Calendar/detailCalendar?calendarSeq=' + item.id,
                                extendedProps: {
                                    type: item.type,
                                    location: item.location,
                                    content: item.content,
                                    user: item.user
                                }
                            });
                        }
                    });

                    successCallback(events);
                },
                error: function(request, status, error) {
                    console.error("Ajax 오류:", request.responseText, error);
                    failureCallback(error);
                }
            });
        },

        // 날짜 클릭 시
        dateClick: function(info) {
            window.location.href = "<%= ctxPath %>/Calendar/addCalendarForm?date=" + info.dateStr;
        }
    });

    calendar.render();
}

// 카테고리 버튼 클릭 시 필터 적용
function loadCategory(category) {
    currentCategory = category;
    calendar.refetchEvents(); // 서버에서 다시 데이터 가져오기
}

// 초기화
document.addEventListener('DOMContentLoaded', initCalendar);
</script>





</body>

</html>

<jsp:include page="../footer/footer.jsp" />
