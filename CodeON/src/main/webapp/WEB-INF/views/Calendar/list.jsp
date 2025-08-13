<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%

	String ctxPath = request.getContextPath();

%>

<html>
<head>

<jsp:include page="/WEB-INF/views/header/header.jsp" />

<title>일정 캘린더</title>

<!-- FullCalendar CSS -->
<link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.css" rel="stylesheet" />

<!-- FullCalendar JS -->
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.js"></script>

<style>
    .container {
        display: flex;
        margin-top: 30px;
        font-family: 'Arial';
    }

    #calendar-sidebar {
        width: 250px;
        padding: 40px;
        border-right: 1px solid #ccc;
    }

    .calendar-category {
        margin-bottom: 20px;
    }

    .calendar-category h4 {
        font-size: 16px;
        border-bottom: 1px solid #ccc;
        padding-bottom: 5px;
        margin-bottom: 10px;
    }

    #calendar-sidebar button {
        display: block;
        width: 100%;
        padding: 8px 10px;
        margin-bottom: 10px;
        margin-top: 40px;    
        font-size: 14px;
        cursor: pointer;
        border: 1px solid #888;
        border-radius: 4px;
        background-color: #f0f0f0;
        transition: background-color 0.2s ease;
    }

    #calendar-sidebar button:hover {
        background-color: #ddd;
    }
    
    #calendar-sidebar {
    	margin-top: 40px;
    }

    .btn-edit, .btn-delete {
        display: none;
    }

    #calendar {
        flex: 1;
        padding: 20px;
        margin-top: 80px;    
        margin-bottom: 80px; 
    }
    
    .fc-day-today {
    background-color: transparent !important;  
    
	}
    
    
    
    
</style>
</head>
<body>

	

<div id="calendar-search" style="margin: 20px 0 0 20px;">
    <form id="searchForm" onsubmit="searchEvents(); return false;">
        <input type="date" id="startDate" name="startDate" />
        ~
        <input type="date" id="endDate" name="endDate" />

        <select id="searchType" name="searchType">
            <option value="">검색 조건</option>
            <option value="title">제목</option>
            <option value="description">내용</option>
            <option value="sharedUser">공유자</option>
        </select>

        <input type="text" id="searchKeyword" name="searchKeyword" placeholder="검색어 입력" />
        <button type="submit">검색</button>
    </form>
</div>

<div class="container">

    <div id="calendar-sidebar">
        <div class="calendar-category">
            <h4></h4>
            <button onclick="showCalendar('사내')">사내 일정</button>
            <button onclick="showCalendar('부서')">부서 일정</button>
            <button onclick="showCalendar('개인')">개인 일정</button>
            <button onclick="showCalendar('공유')">공유 일정</button>
        </div>
    </div>

    <div id="calendar"></div>
</div>


<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@fullcalendar/google-calendar@6.1.7/index.global.min.js"></script>

<script>

//=== 검색 기능 === //
function goSearch(){

	if( $("#fromDate").val() > $("#toDate").val() ) {
		alert("검색 시작날짜가 검색 종료날짜 보다 크므로 검색할 수 없습니다.");
		return;
	}
    
	if( $("select#searchType").val()=="" && $("input#searchWord").val()!="" ) {
		alert("검색대상 선택을 해주세요!!");
		return;
	}
	
	if( $("select#searchType").val()!="" && $("input#searchWord").val()=="" ) {
		alert("검색어를 입력하세요!!");
		return;
	}
	
   	var frm = document.searchScheduleFrm;
    frm.method="get";
    frm.action="<%= ctxPath%>/schedule/searchSchedule";
    frm.submit();
	
}// end of function goSearch(){}--------------------------

    let calendar;

    // 내부 일정 예시
    const events = [
        { title: '사내 회의', start: '2025-08-10', color: '#1E90FF', type: '사내' },
        { title: '부서 회의', start: '2025-08-12', color: '#FF6347', type: '부서' },
        { title: '개인 업무', start: '2025-08-14', color: '#32CD32', type: '개인' },
        { title: '공유 일정', start: '2025-08-16', color: '#FFD700', type: '공유' }
    ];

    function showCalendar(category) {
        if (calendar) {
            calendar.destroy();
        }

        const calendarEl = document.getElementById('calendar');
        calendarEl.innerHTML = '';

        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },

            googleCalendarApiKey: "AIzaSyASM5hq3PTF2dNRmliR_rXpjqNqC-6aPbQ",  // 본인 API 키로 교체

            eventSources: [
                {
                    events: events.filter(e => e.type === category),
                },
                {
                    googleCalendarId: 'ko.south_korea#holiday@group.v.calendar.google.com',
                    googleCalendar: true,
                    color: 'red',
                    textColor: 'white'
                }
            ],

            dateClick: function(info) {
                // 클릭한 날짜
                const selectedDate = info.dateStr;
                // 현재 캘린더 카테고리 (showCalendar 매개변수 category 사용)
                const selectedCategory = category;

                // 일정 등록 페이지로 이동 (쿼리스트링에 날짜와 카테고리 전달)
                window.location.href = "<%= ctxPath %>/Calendar/addCalendarForm?date=" 
                                       + selectedDate 
                                       + "&category=" 
                                       + encodeURIComponent(selectedCategory);
            }

        });

        calendar.render();
    }

    document.addEventListener('DOMContentLoaded', function() {
        showCalendar('사내');
    });

    function searchEvents() {
        alert('검색 기능은 아직 구현되지 않았습니다.');
    }
    
    
    
    
</script>

</body>
</html>

<jsp:include page="../footer/footer.jsp" />
