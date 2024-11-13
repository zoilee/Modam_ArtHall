//예약정보 ajax
$(document).ready(function () {

    // 뮤지컬 날짜, 회차, 인원 선택
    $.ajax({
        url: '/api/musicals',
        type: 'POST',
        success: function (response) {
            var musicalId = response.musicalId;
            var musicalTitle = response.musicalTitle;
            var selected_date = $("#select-date").val();
            var selected_time = $("#select-time-btn").val();
            var numberOfPeople = $("#num-select").val();
        },
        error: function () {
            alert("예약 처리 중 문제가 발생했습니다.");
        }
    });

    

    // 5. 예약하기
    $('#reserve-button').click(function () {
        var showId = $('#show-select').val();
        var numberOfPeople = selectedSeats.length;
        $.ajax({
            url: '/api/reservations',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                showId: showId,
                numberOfPeople: numberOfPeople,
                reservedSeats: selectedSeats
            }),
            success: function () {
                alert('예약이 완료되었습니다.');
                location.reload(); // 예약 후 페이지 리로드
            },
            error: function () {
                alert('예약에 실패했습니다.');
            }
        });
    });
});