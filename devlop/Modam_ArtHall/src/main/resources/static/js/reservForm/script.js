$(document).ready(function () {
    //뮤지컬 아이디 받기
    const musicalId = $("#selectedMusical").val();


    //다음 페이지로 정보 넘기기
    const form = $('<form></form>');
    form.attr('method', 'POST');
    form.attr('action', '/modam/seatSelect');  // 정보를 전달할 서버 경로

    // 선택된 musical ID를 폼에 추가
    const musicalIdInput = $('<input>').attr('type', 'hidden').attr('name', 'musicalId').val(musicalId);
    form.append(musicalIdInput);

    // 선택한 날짜, 회차, 인원, 좌석 정보를 폼에 추가
    const selectedDateInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedDate').val(selectedDate);
    const selectedTimeInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedTime').val(selectedTime);
    const numberOfPeopleInput = $('<input>').attr('type', 'hidden').attr('name', 'numberOfPeople').val(numberOfPeople);

    form.append(selectedDateInput);
    form.append(selectedTimeInput);
    form.append(numberOfPeopleInput);

    // 폼을 body에 추가하고 제출
    $('body').append(form);
    form.submit();

    //회차선택 버튼 누르면 표시,hidden에 값 주기
    $(".timeselect-btn").click(function(){
        const timeButton = $(this);
        const selectedTime = document.querySelector("#selectedTime");
        if (timeButton.hasClass('selected')) {
            timeButton.removeClass('selected');
            selectedTime.value = '';
        } else {
            seatButton.addClass('selected');
            selectedTime.value = timeButton.val();
        }
    })
    
});