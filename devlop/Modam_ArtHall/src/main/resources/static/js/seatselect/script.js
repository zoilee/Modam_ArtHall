let selectedSeats = new Array();
const seatWrapper = document.querySelector(".seat-wrapper");

// 좌석 번호를 매핑하는 함수
function mapping(input, i, j) {
    const rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L'];
    input.value = rows[i] + (j + 1);  // 행(알파벳)과 열(숫자)을 결합
}

// 좌석 등급에 따라 클래스를 추가하는 함수
function setClassBasedOnValue(input) {
    const value = input.value;
    const row = value[0];  // 행 (예: A, B, C...)
    const col = parseInt(value.substring(1));  // 열 (숫자 부분)

    // vip 클래스 조건
    if (
        (row === 'A' || row === 'B' || row === 'C') && col >= 4 && col <= 15
    ) {
        input.classList.add("vip");
    }
    // r 클래스 조건
    else if (
        (row === 'A' && col >= 1 && col <= 3) || (row === 'A' && col >= 16 && col <= 18) ||
        (row === 'B' && col >= 1 && col <= 3) || (row === 'B' && col >= 16 && col <= 18) ||
        (row === 'C' && col >= 1 && col <= 3) || (row === 'C' && col >= 16 && col <= 18) ||
        row === 'D' ||
        (row === 'E' || row === 'F' || row === 'G' || row === 'H') && col >= 4 && col <= 15
    ) {
        input.classList.add("r");
    }
    // s 클래스 조건
    else if (
        (row === 'E' && col >= 1 && col <= 3) || (row === 'E' && col >= 16 && col <= 18) ||
        (row === 'F' && col >= 1 && col <= 3) || (row === 'F' && col >= 16 && col <= 18) ||
        (row === 'G' && col >= 1 && col <= 3) || (row === 'G' && col >= 16 && col <= 18) ||
        (row === 'H' && col >= 1 && col <= 3) || (row === 'H' && col >= 16 && col <= 18) ||
        row === 'I' ||
        (row === 'J' && col >= 4 && col <= 15)
    ) {
        input.classList.add("s");
    }
    // 그 외 a 클래스
    else {
        input.classList.add("a");
    }
}

// 좌석을 생성하는 함수
function createSeats() {
    const rows = 12;  // 세로 12줄
    const columns = 18;  // 가로 18줄

    // 좌석을 12줄 x 18열로 생성
    for (let i = 0; i < rows; i++) {
        for (let j = 0; j < columns; j++) {
            // input 요소 생성
            const seat = document.createElement('input');
            seat.type = 'button';
            seat.classList.add('seat');  // 'seat' 클래스 추가

            // 좌석 번호 매핑
            mapping(seat, i, j);
            // 좌석에 클래스 추가
            setClassBasedOnValue(seat);

            // 생성된 좌석을 seat-wrapper에 추가
            seatWrapper.appendChild(seat);
        }
    }
}

// 페이지가 로드되면 좌석 생성 함수 호출
window.onload = createSeats;

// 좌석 선택 이벤트
$(document).on('click', '.seat', function () {
    var seatButton = $(this);
    var seatId = seatButton.val();
    const seatPicked1 = document.querySelector("#seatPicked1");
    const seatPicked2 = document.querySelector("#seatPicked2");

    // numberOfPeople 값 가져오기 (숨겨진 input에서)
    const numberOfPeople = parseInt($('input[name="numberOfPeople"]').val(), 10);

    // 이미 선택된 좌석이 2개 이상일 경우 추가 선택 막기
    if (numberOfPeople === 1 && selectedSeats.length >= 1 && !seatButton.hasClass('selected')) {
        alert("하나의 좌석만 선택할 수 있습니다.");
        return; // 1명이 선택일 경우 1개 이상의 좌석만 선택할 수 있도록 제한
    }
    if (numberOfPeople === 2 && selectedSeats.length >= 2 && !seatButton.hasClass('selected')) {
        alert("최대 2개의 좌석만 선택할 수 있습니다.");
        return; // 2명이 선택일 경우 2개 이상의 좌석만 선택할 수 있도록 제한
    }

    if (seatButton.hasClass('selected')) {
        // 선택 해제
        seatButton.removeClass('selected');
        selectedSeats = selectedSeats.filter(function (id) {
            return id !== seatId;
        });
    } else {
        // 좌석 선택
        seatButton.addClass('selected');
        selectedSeats.push(seatId);
    }

    // 선택된 좌석 표시
    seatPicked1.textContent = selectedSeats[0] || ''; // 첫 번째 선택된 좌석
    seatPicked2.textContent = selectedSeats[1] || ''; // 두 번째 선택된 좌석

    // 선택된 좌석 정보를 폼에 추가
    updateForm();
});

// 폼 지정
const form = document.querySelector("#formToReservConfirm");

// 선택된 좌석 정보를 폼에 추가
function updateForm() {
    // 기존의 hidden input 제거 (폼 업데이트 시마다 새로 추가)
    const existingInput1 = document.querySelector('input[name="seatId1"]');
    const existingInput2 = document.querySelector('input[name="seatId2"]');

    if (existingInput1) existingInput1.remove();
    if (existingInput2) existingInput2.remove();

    // 선택된 좌석을 폼에 추가
    const selectedSeatsInput1 = $('<input>').attr('type', 'hidden').attr('name', 'seatId1').val(selectedSeats[0] || '');
    const selectedSeatsInput2 = $('<input>').attr('type', 'hidden').attr('name', 'seatId2').val(selectedSeats[1] || '');

    // 선택된 좌석 정보를 폼에 추가
    form.append(selectedSeatsInput1);
    form.append(selectedSeatsInput2);
}

// 폼을 body에 추가하고 제출
$('#toReservConfirm').on('click', function() {
    form.submit();
});