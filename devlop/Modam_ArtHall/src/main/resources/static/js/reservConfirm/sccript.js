$(document).ready(function(){
    $('#confirm-reserv-button').click(function () {
        const musicalId = $('musicalIdInput').val();
        const selectedDate = $("selectedDateInput").val();
        const selectedTime = $("selectedTimeInput").val();
        const numberOfPeople = $("numberOfPeopleInput").val();
        const selectedSeats = $("selectedSeatsInput").val();
        const vipValue = 120000;
        const rValue = 100000;
        const sValue = 80000;
        const aValue = 60000;
        $.ajax({
            url: '/modam/seatSelect',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                musicalId: musicalId,
                selectedDate: selectedDate,
                selectedTime: selectedTime,
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
