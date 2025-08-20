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

<div class="container">
    <div id="calendar-sidebar">
        <button onclick="loadCategory('사내')">사내 일정</button>
        <button onclick="loadCategory('부서')">부서 일정</button>
        <button onclick="loadCategory('개인')">개인 일정</button>
        <button onclick="loadCategory('공유')">공유 일정</button>
    </div>

    <div id="calendar"></div>
</div>

<script>
let calendar;
let currentCategory = '사내'; // 기본 카테고리

function initCalendar() {
    const calendarEl = document.getElementById('calendar');

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'ko',
        headerToolbar: { left: 'prev,next today', center: 'title', right: 'dayGridMonth,timeGridWeek,timeGridDay' },

        // === 구글캘린더를 이용하여 대한민국 공휴일 표시하기 시작 === //
           //   googleCalendarApiKey : "자신의 Google API KEY 입력",
              /*
                  >> 자신의 Google API KEY 을 만드는 방법 <<
                  1. 먼저 크롬 웹브라우저를 띄우고, 자신의 구글 계정으로 로그인 한다.
                  2. https://console.developers.google.com 에 접속한다. 
                  3. "API  API 및 서비스" 에서 "사용자 인증 정보" 를 클릭한다.
                  4. ! 이 페이지를 보려면 프로젝트를 선택하세요 에서 "프로젝트 만들기" 를 클릭한다.
                  5. 프로젝트 이름은 자동으로 나오는 값을 그대로 두고 그냥 "만들기" 버튼을 클릭한다. 
                  6. 상단에 보여지는 사용자 인증 정보 옆의  "+ 사용자 인증 정보 만들기" 를 클릭하여 보여지는 API 키를 클릭한다.
                                  그러면 API 키 생성되어진다.
                  7. 생성된 API 키가  자신의 Google API KEY 이다.
                  8. 애플리케이션에 대한 정보를 포함하여 OAuth 동의 화면을 구성해야 합니다. 에서 "동의 화면 구성" 버튼을 클릭한다.
                  9. OAuth 동의 화면 --> User Type --> 외부를 선택하고 "만들기" 버튼을 클릭한다.
                 10. 앱 정보 --> 앱 이름에는 "웹개발테스트"
                           --> 사용자 지원 이메일에는 자신의 구글계정 이메일 입력
                                   하단부에 보이는 개발자 연락처 정보 --> 이메일 주소에는 자신의 구글계정 이메일 입력 
                 11. "저장 후 계속" 버튼을 클릭한다. 
                 12. 범위 --> "저장 후 계속" 버튼을 클릭한다.
                 13. 테스트 사용자 --> "저장 후 계속" 버튼을 클릭한다.
                 14. "API  API 및 서비스" 에서 "라이브러리" 를 클릭한다.
                     Google Workspace --> Google Calendar API 를 클릭한다.
                     "사용" 버튼을 클릭한다. 
              */
                googleCalendarApiKey : "AIzaSyASM5hq3PTF2dNRmliR_rXpjqNqC-6aPbQ",
                 eventSources :[ 
                  {
                  //  googleCalendarId : '대한민국의 휴일 캘린더 통합 캘린더 ID'
                      googleCalendarId : 'ko.south_korea#holiday@group.v.calendar.google.com'
                    , color: 'white'   // 옵션임! 옵션참고 사이트 https://fullcalendar.io/docs/event-source-object
                    , textColor: 'red' // 옵션임! 옵션참고 사이트 https://fullcalendar.io/docs/event-source-object 
                  } 
              ],
           // === 구글캘린더를 이용하여 대한민국 공휴일 표시하기 끝 === //
        
        
        
        // ===================== DB 와 연동하는 Ajax 이벤트 ===================== //
        events: function(fetchInfo, successCallback, failureCallback) {
            $.ajax({
                url: '<%= ctxPath%>/calendar/selectCalendar',
                dataType: "json",
                success: function(json) {
                    var events = [];
                    if(json.length > 0) {
                        $.each(json, function(index, item) {
                            var start = item.start ? item.start.replace(" ", "T") : null;
                            var end = item.end ? item.end.replace(" ", "T") : start;

                            // 카테고리 필터 적용
                            if(currentCategory === item.type) {
                                events.push({
                                    id: item.id,
                                    title: item.title,
                                    start: start,
                                    end: end,
                                    color: item.color || '#3788d8',
                                    url: '<%= ctxPath %>/Calendar/detailCalendar?calendarSeq=' + item.id,
                                    user: item.user
                                });
                            }
                        });
                    }
                    successCallback(events);
                },
                error: function(request, status, error) {
                    console.error("Ajax 오류:", request.responseText, error);
                    failureCallback(error);
                }
            });
        },
        // ===================== DB 와 연동하는 Ajax 이벤트 끝 ===================== //

        dateClick: function(info) {
            window.location.href = "<%= ctxPath %>/Calendar/addCalendarForm?date=" + info.dateStr;
        }
    });

    calendar.render();
}

// 카테고리 버튼 클릭 시
function loadCategory(category) {
    currentCategory = category;
    calendar.refetchEvents();
}

// 초기화
document.addEventListener('DOMContentLoaded', initCalendar);
</script>

</body>
</html>

<jsp:include page="../footer/footer.jsp" />
