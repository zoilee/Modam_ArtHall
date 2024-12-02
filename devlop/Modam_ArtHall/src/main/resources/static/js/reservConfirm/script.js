$(document).ready(function(){
    //좌석가격지정
    const vipValue = 200000;
    const rValue = 100000;
    const sValue = 80000;
    const aValue = 60000;


// 좌석 ID에 따라 가격을 설정하는 함수
function getSeatPrice(seatId) {
    // VIP 좌석 (200000원)
    const vipSeats = [
        'a4', 'a5', 'a6', 'a7', 'a8', 'a9', 'a10', 'a11', 'a12', 'a13', 'a14', 'a15', 
        'b4', 'b5', 'b6', 'b7', 'b8', 'b9', 'b10', 'b11', 'b12', 'b13', 'b14', 'b15', 
        'c4', 'c5', 'c6', 'c7', 'c8', 'c9', 'c10', 'c11', 'c12', 'c13', 'c14', 'c15'
    ];
    
    // R 좌석 (100000원)
    const rSeats = [
        'a1', 'a2', 'a3', 'a16', 'a17', 'a18', 'b1', 'b2', 'b3', 'b16', 'b17', 'b18',
        'c1', 'c2', 'c3', 'c16', 'c17', 'c18', 'd1', 'd2', 'd3', 'd4', 'd5', 'd6', 'd7',
        'd8', 'd9', 'd10', 'd11', 'd12', 'd13', 'd14', 'd15', 'd16', 'd17', 'd18',
        'e4', 'e5', 'e6', 'e7', 'e8', 'e9', 'e10', 'e11', 'e12', 'e13', 'e14', 'e15',
        'f4', 'f5', 'f6', 'f7', 'f8', 'f9', 'f10', 'f11', 'f12', 'f13', 'f14', 'f15',
        'g4', 'g5', 'g6', 'g7', 'g8', 'g9', 'g10', 'g11', 'g12', 'g13', 'g14', 'g15',
        'h4', 'h5', 'h6', 'h7', 'h8', 'h9', 'h10', 'h11', 'h12', 'h13', 'h14', 'h15'
    ];

    // S 좌석 (80000원)
    const sSeats = [
        'e1', 'e2', 'e3', 'e16', 'e17', 'e18', 'f1', 'f2', 'f3', 'f16', 'f17', 'f18', 
        'g1', 'g2', 'g3', 'g16', 'g17', 'g18', 'h1', 'h2', 'h3', 'h16', 'h17', 'h18',
        'i1', 'i2', 'i3', 'i4', 'i5', 'i6', 'i7', 'i8', 'i9', 'i10', 'i11', 'i12', 
        'i13', 'i14', 'i15', 'i16', 'i17', 'i18', 'j4', 'j5', 'j6', 'j7', 'j8', 
        'j9', 'j10', 'j11', 'j12', 'j13', 'j14', 'j15'
    ];

    // 각 좌석 ID에 따른 가격을 반환
    if (vipSeats.includes(seatId)) {
        return vipValue;
    } else if (rSeats.includes(seatId)) {
        return rValue;
    } else if (sSeats.includes(seatId)) {
        return sValue;
    } else {
        return aValue;
    }
}

// 선택 좌석의 가격을 설정하는 함수
function setPriceAndSubmit() {
    const seatId1 = document.querySelector('input[name="seatId1"]').value;
    const seatId2 = document.querySelector('input[name="seatId2"]').value;
    const seatPrice1 = getSeatPrice(seatId1);
    const seatPrice2 = getSeatPrice(seatId2);

    // 선택된 좌석 가격을 명세서에 할당
    document.querySelector('#seatPrice1').textContent = seatPrice1.toLocaleString();
    document.querySelector('#seatPrice2').textContent = seatPrice2.toLocaleString();

    // 전체 금액 계산 및 표시
    const totalPrice = seatPrice1 + seatPrice2;
    document.querySelector('#totalPrice').textContent = totalPrice.toLocaleString();
}
// 페이지 로드 후 가격 설정
window.onload = setPriceAndSubmit



//showTime 시간으로 변환해서 보여주기
function transformToTime(){
    // .selectedTimeConfirm 요소에서 showTime 값을 가져옴
    const timeShow = document.querySelector(".selectedTimeConfirm");
    const selectedTime = timeShow.getAttribute("data-show-time"); // showTime 값 가져오기

    if (selectedTime == 1) {
        timeShow.textContent = "13:00"; // 시간 출력
    } else {
        timeShow.textContent = "17:00";
    }
}

// 페이지 로드 후 실행
transformToTime();

   


    $('#confirm-reserv-button').on('click', function() {
            const reservationData = {
                showId: showId,
                seatId1: seatId1,
                seatId2: seatId2,
                totalPrice: totalPrice,
                status: 'CONFIRMED',
                userId: getSessionUserId()
            };
        
            $.ajax({
                url: '/modam/reservConfirm',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(reservationData),
                success: function(response) {
                    if (response.success) {
                        alert('예약이 완료되었습니다.');
                    } else {
                        alert('예약 실패: ' + response.message);
                    }
                },
                error: function(error) {
                    console.error('예약 처리 중 오류 발생:', error);
                    alert('예약 처리 중 오류가 발생했습니다.');
                }
            });
        });
    
});