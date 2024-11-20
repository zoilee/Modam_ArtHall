 // 페이지가 로드될 때 현재 날짜를 'today' 값을 기반으로 초기화
 document.addEventListener('DOMContentLoaded', function() {
    const calendarInput = document.getElementById('calendarInput');
    const hiddenDate = document.getElementById('hiddenDate');
    
    // 기본값을 서버에서 전달된 오늘 날짜로 설정 (예: "2024-11-20")
    const today = calendarInput.value;
    hiddenDate.value = today; // 'hiddenDate' input에 기본 오늘 날짜 설정
    
    // 날짜가 선택될 때마다 hidden input에 선택된 날짜 반영
    calendarInput.addEventListener('change', function() {
        hiddenDate.value = calendarInput.value;
    });
});

//선택값으로 show id 추출하기
document.addEventListener('DOMContentLoaded', function() {
    const showIdField = document.getElementById('showId');
    const calendarInput = document.getElementById('calendarInput');
    const showTimeSelect = document.getElementById('selectedTime');
    const performanceIdField = document.getElementById('selectedPerformanceId')
    
    // 초기값을 서버에서 전달된 값으로 설정
    const performanceId = performanceIdField.value;
    const today = calendarInput.value; // 기본값을 오늘 날짜로 설정
    
    // 날짜와 회차가 변경될 때마다 showId를 업데이트
    function fetchShowId() {
        const selectedDate = calendarInput.value;
        const selectedTime = showTimeSelect.value;
        
        if (selectedDate && selectedTime) {
            // 선택된 날짜와 회차를 서버에 요청
            fetch(`/getShowId?performanceId=${performanceId}&showDate=${selectedDate}&showTime=${selectedTime}`)
                .then(response => response.json())
                .then(data => {
                    if (data.showId) {
                        showIdField.value = data.showId; // showId를 hidden input에 업데이트
                    } else {
                        alert('해당 회차의 공연 정보를 찾을 수 없습니다.');
                    }
                })
                .catch(error => console.error('Error fetching showId:', error));
        }
    }

    // 날짜 또는 회차가 변경될 때마다 showId 업데이트
    calendarInput.addEventListener('change', fetchShowId);
    showTimeSelect.addEventListener('change', fetchShowId);
    
    // 초기 값에 대한 showId 가져오기
    fetchShowId();
});

$(document).ready(function () {

    //회차선택 버튼 누르면 표시,hidden에 값 주기
    $(".timeselect-btn").click(function(){
        const timeButton = $(this);
        const selectedTime = document.querySelector("#selectedTime");
        if (timeButton.hasClass('selected')) {
            timeButton.removeClass('selected');
            selectedTime.value = '';
        } else {
            timeButton.addClass('selected');
            selectedTime.value = timeButton.val();
        }
    });

    //

    //인원수
    const numberOfPeople = $("#numberOfPeople").val();

    //폼 지정
    const form = document.querySelector("#formToReservConfirm");

    // 선택한 날짜, 회차, 인원, 좌석 정보를 폼에 추가
    const selectedDateInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedDate').val(selectedDate);
    const selectedTimeInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedTime').val(selectedTime);
    const numberOfPeopleInput = $('<input>').attr('type', 'hidden').attr('name', 'numberOfPeople').val(numberOfPeople);

    form.append(selectedDateInput);
    form.append(selectedTimeInput);
    form.append(numberOfPeopleInput);
    
    form.submit();
    
});