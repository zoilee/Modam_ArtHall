document.addEventListener('DOMContentLoaded', function() {
    const calendarInput = document.getElementById('calendarInput');
    const selectDate = document.getElementById('showDate');
    const showTimeSelect = document.getElementById('showTime');
    const performanceIdField = document.getElementById('PerformanceId');
    const today = calendarInput.value;
    selectDate.value = today;

    // 날짜가 선택될 때마다 hidden input에 선택된 날짜 반영
    calendarInput.addEventListener('change', function() {
        selectDate.value = calendarInput.value;
    });

    // 선택된 날짜와 회차를 기반으로 showId를 가져오는 함수
    let gettedshowId = '';
    function fetchShowId() {
        const selectedDate = calendarInput.value;
        const selectedTime = showTimeSelect.value;
        
        if (selectedDate && selectedTime) {
            fetch(`/getShowId?performanceId=${performanceIdField.value}&showDate=${selectedDate}&showTime=${selectedTime}`)
                .then(response => response.json())
                .then(data => {
                    if (data.showId) {
                        gettedshowId = data.showId;
                    } else {
                        alert('해당 회차의 공연 정보를 찾을 수 없습니다.');
                    }
                })
                .catch(error => console.error('showId를 가져오는 도중 문제가 발생했습니다:', error));
        }
    }

    calendarInput.addEventListener('change', fetchShowId);
    showTimeSelect.addEventListener('change', fetchShowId);
    fetchShowId();
});

$(document).ready(function () {

    //인원수
    const numberOfPeople = $("#numberOfPeople").val();
    const selectedTime = $("#showTime").val();

    //폼 지정
    const form1 = document.querySelector("#formToReservConfirm");
    const form2 = document.querySelector("#formToseatSelect");

    // 선택한 회차, 인원 정보를 폼에 추가
    const selectedTimeInput = $('<input>').attr('type', 'hidden').attr('name', 'showTime').val(selectedTime);
    const numberOfPeopleInput = $('<input>').attr('type', 'hidden').attr('name', 'numberOfPeople').val(numberOfPeople);
    const showIdInput = $('<input>').attr('type', 'hidden').attr('name', 'showId').val(gettedshowId);

    form1.append(selectedTimeInput);
    form1.append(numberOfPeopleInput);
    form1.append(showIdInput);

    form2.append(showIdInput.clone());
    
    $('#toSeatSelect').on('click', function() {
        form1.submit();
        form2.submit();
    });
});