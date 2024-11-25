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
        fetchShowId(); // 날짜가 변경될 때마다 showId를 새로 가져오기
    });

    // 'showTime' 값이 변경될 때마다 폼을 업데이트
    $('#showTime').on('change', function() {
        const selectedTime = $(this).val();
        updateForm(selectedTime, $('#numberOfPeople').val());
    });

    // 'numberOfPeople' 값이 변경될 때마다 폼을 업데이트
    $('#numberOfPeople').on('change', function() {
        const numberOfPeople = $(this).val();
        updateForm($('#showTime').val(), numberOfPeople);
    });

    let gettedshowId = ''; // showId를 저장할 변수

    // 선택된 날짜와 회차를 기반으로 showId를 가져오는 함수
    function fetchShowId() {
        const selectedDate = calendarInput.value;
        const selectedTime = showTimeSelect.value;
    
        if (selectedDate && selectedTime) {
            fetch(`/getShowId?performanceId=${performanceIdField.value}&showDate=${selectedDate}&showTime=${selectedTime}`)
                .then(response => response.json())
                .then(data => {
                    if (data.showId) {
                        gettedshowId = data.showId; // showId 업데이트
                        updateForm($('#showTime').val(), $('#numberOfPeople').val()); // 폼을 업데이트
                    } else {
                        alert('해당 회차의 공연 정보를 찾을 수 없습니다.');
                    }
                })
                .catch(error => console.error('showId를 가져오는 도중 문제가 발생했습니다:', error));
        }
    }

    // 선택된 회차와 인원 수를 폼에 추가하는 함수
    function updateForm(selectedTime, numberOfPeople) {
        // 폼 요소 지정
        const form1 = document.querySelector("#formToReservConfirm");
        const form2 = document.querySelector("#formToseatSelect");

        // 기존 폼 필드 제거 (업데이트 시 기존 값 삭제)
        form1.querySelector('input[name="showTime"]')?.remove();
        form1.querySelector('input[name="numberOfPeople"]')?.remove();
        form1.querySelector('input[name="showId"]')?.remove();

        form2.querySelector('input[name="numberOfPeople"]')?.remove();
        form2.querySelector('input[name="showId"]')?.remove();

        // 선택된 값으로 새로운 입력 필드 추가
        if (gettedshowId) { // showId가 존재할 때만 추가
            const selectedTimeInput = $('<input>').attr('type', 'hidden').attr('name', 'showTime').val(selectedTime);
            const numberOfPeopleInput = $('<input>').attr('type', 'hidden').attr('name', 'numberOfPeople').val(numberOfPeople);
            const showIdInput = $('<input>').attr('type', 'hidden').attr('name', 'showId').val(gettedshowId);

            form1.append(selectedTimeInput);
            form1.append(numberOfPeopleInput);
            form1.append(showIdInput);

            form2.append(showIdInput.clone());
            form2.append(numberOfPeopleInput.clone());
        }
    }

    // 페이지가 처음 로드될 때 초기 폼 값 설정
    fetchShowId();
});

$(document).ready(function () {
    $('#toSeatSelect').on('click', function() {
        // 폼 제출 전 유효성 검사
        if (gettedshowId) {
            $("#formToReservConfirm").submit();
            $("#formToseatSelect").submit();
        } else {
            alert("공연 회차 정보를 불러오지 못했습니다. 다시 시도해주세요.");
        }
    });
});
