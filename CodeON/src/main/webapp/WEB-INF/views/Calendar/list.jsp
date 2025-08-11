<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>일정 캘린더</title>

    <link href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.7/index.global.min.css" rel="stylesheet" />
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

        /* 버튼 스타일 */
        #calendar-sidebar button {
            display: block;
            width: 100%;
            padding: 8px 10px;
            margin-bottom: 10px;
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

        /* 기존 수정, 삭제 버튼 숨김 (필요하면 다시 살릴 수 있음) */
        .btn-edit, .btn-delete {
            display: none;
        }

        #calendar {
            flex: 1;
            padding: 20px;
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
    <!-- 왼쪽: 카테고리별 버튼 -->
    <div id="calendar-sidebar">
        <div class="calendar-category">
            <h4>캘린더 종류 선택</h4>
            <button onclick="showCalendar('사내')">사내 일정</button>
            <button onclick="showCalendar('부서')">부서 일정</button>
            <button onclick="showCalendar('개인')">개인 일정</button>
            <button onclick="showCalendar('공유')">공유 일정</button>
        </div>
    </div>

    <!-- 오른쪽: 캘린더 표시 -->
    <div id="calendar"></div>
</div>

<script>

    let calendar;

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
        calendarEl.innerHTML = '';  // 캘린더 div 비우기 (안해도 되지만 안전하게)

        // 선택한 카테고리 이벤트만 필터링
        const filteredEvents = events.filter(e => e.type === category);

        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            events: filteredEvents,
            eventColor: filteredEvents.length > 0 ? filteredEvents[0].color : undefined,
            locale: 'ko',   // ← 이 부분 추가
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: 'dayGridMonth,timeGridWeek,timeGridDay'
            },
            dateClick: function(info) {
                alert(info.dateStr + " - " + category + " 일정 선택됨");
            }
        });


        calendar.render();
    }

    document.addEventListener('DOMContentLoaded', function() {
        showCalendar('사내');  // 기본으로 사내 일정 보여주기
    });

    function searchEvents() {
        // 검색 기능 구현 위치 (필요하면 추가 가능)
        alert('검색 기능은 아직 구현되지 않았습니다.');
    }
</script>

</body>
</html>
