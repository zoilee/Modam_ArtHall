$(document).ready(function(){
    const musicalId = $('musicalIdInput').val();
    const selectedDate = $("selectedDateInput").val();
    const selectedTime = $("selectedTimeInput").val();
    const numberOfPeople = $("numberOfPeopleInput").val();
    const selectedSeat1 = $("selectedSeatsInput1").val();
    const selectedSeat2 = $("selectedSeatsInput2").val();
    const seat1price = '';
    const seat2Price = '';
    const totalSeatPrice = seat1price + seat2Price;
    const vipValue = 200000;
    const rValue = 100000;
    const sValue = 80000;
    const aValue = 60000;

    //selectedTime 시간으로 변환해서 출력
    function transformToTime(selectedTime){
        const timeShow = document.querySelector("#showConfirmTime");
        if(selectedTime == 1){
            timeShow.append("13:00");
        }else{
            timeShow.append("17:00");
        }
    };
    transformToTime(selectedTime);

    //좌석 위치별 가격 설정
    if ((selectedSeat1 >= "A4" && selectedSeat1 <= "A15") ||
    (selectedSeat1 >= "B4" && selectedSeat1 <= "B15") ||
    (selectedSeat1 >= "C4" && selectedSeat1 <= "C15")) {
        seat1price = vipValue;
    }
    else if (
        (selectedSeat1 >= "A1" && selectedSeat1 <= "A3") ||
        (selectedSeat1 >= "A16" && selectedSeat1 <= "A18") ||
        (selectedSeat1 >= "B1" && selectedSeat1 <= "B3") ||
        (selectedSeat1 >= "B16" && selectedSeat1 <= "B18") ||
        (selectedSeat1 >= "C1" && selectedSeat1 <= "C3") ||
        (selectedSeat1 >= "C16" && selectedSeat1 <= "C18") ||
        (selectedSeat1 >= "D1" && selectedSeat1 <= "D18") ||
        (selectedSeat1 >= "E4" && selectedSeat1 <= "E15") ||
        (selectedSeat1 >= "F4" && selectedSeat1 <= "F15") ||
        (selectedSeat1 >= "G4" && selectedSeat1 <= "G15") ||
        (selectedSeat1 >= "H4" && selectedSeat1 <= "H15")
    ) {
       seat1price = rValue;
    }
    else if (
        (selectedSeat1 >= "E1" && selectedSeat1 <= "E3") ||
        (selectedSeat1 >= "E16" && selectedSeat1 <= "E18") ||
        (selectedSeat1 >= "F1" && selectedSeat1 <= "F3") ||
        (selectedSeat1 >= "F16" && selectedSeat1 <= "F18") ||
        (selectedSeat1 >= "G1" && selectedSeat1 <= "G3") ||
        (selectedSeat1 >= "G16" && selectedSeat1 <= "G18") ||
        (selectedSeat1 >= "H1" && selectedSeat1 <= "H3") ||
        (selectedSeat1 >= "H16" && selectedSeat1 <= "H18") ||
        (selectedSeat1 >= "I1" && selectedSeat1 <= "I18") ||
        (selectedSeat1 >= "J4" && selectedSeat1 <= "J15")
    ) {
        seat1price = sValue;
    }
    else{
        seat1price = aValue;
    }

    if ((selectedSeat2 >= "A4" && selectedSeat2 <= "A15") ||
    (selectedSeat2 >= "B4" && selectedSeat2 <= "B15") ||
    (selectedSeat2 >= "C4" && selectedSeat2 <= "C15")) {
        seat2Price = vipValue;
    }
    else if (
        (selectedSeat2 >= "A1" && selectedSeat2 <= "A3") ||
        (selectedSeat2 >= "A16" && selectedSeat2 <= "A18") ||
        (selectedSeat2 >= "B1" && selectedSeat2 <= "B3") ||
        (selectedSeat2 >= "B16" && selectedSeat2 <= "B18") ||
        (selectedSeat2 >= "C1" && selectedSeat2 <= "C3") ||
        (selectedSeat2 >= "C16" && selectedSeat2 <= "C18") ||
        (selectedSeat2 >= "D1" && selectedSeat2 <= "D18") ||
        (selectedSeat2 >= "E4" && selectedSeat2 <= "E15") ||
        (selectedSeat2 >= "F4" && selectedSeat2 <= "F15") ||
        (selectedSeat2 >= "G4" && selectedSeat2 <= "G15") ||
        (selectedSeat2 >= "H4" && selectedSeat2 <= "H15")
    ) {
        seat2Price = rValue;
    }
    else if (
        (selectedSeat2 >= "E1" && selectedSeat2 <= "E3") ||
        (selectedSeat2 >= "E16" && selectedSeat2 <= "E18") ||
        (selectedSeat2 >= "F1" && selectedSeat2 <= "F3") ||
        (selectedSeat2 >= "F16" && selectedSeat2 <= "F18") ||
        (selectedSeat2 >= "G1" && selectedSeat2 <= "G3") ||
        (selectedSeat2 >= "G16" && selectedSeat2 <= "G18") ||
        (selectedSeat2 >= "H1" && selectedSeat2 <= "H3") ||
        (selectedSeat2 >= "H16" && selectedSeat2 <= "H18") ||
        (selectedSeat2 >= "I1" && selectedSeat2 <= "I18") ||
        (selectedSeat2 >= "J4" && selectedSeat2 <= "J15")
    ) {
        seat2Price = sValue;
    }
    else{
        seat2Price = aValue;
    }

    $('#confirm-reserv-button').click(function () {
        $.ajax({
            url: '/modam/reservConfirm',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                performanceId: performanceId,
                selectedDate: selectedDate,
                selectedTime: selectedTime,
                numberOfPeople: numberOfPeople,
                reservedSeat1: selectedSeat1,
                reservedSeat2: selectedSeat2,
                totalSeatPrice: totalSeatPrice
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