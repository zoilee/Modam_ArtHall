let selectedSeats = new Array();
const seatWrapper = document.querySelector(".seat-wrapper");
const seatPicked1 = document.querySelector("#seatPicked1");
const seatPicked2 = document.querySelector("#seatPicked2");
let div = "";

for (let i = 0; i < 12; i++) {
    div = document.createElement("div");
    seatWrapper.append(div);
    for (let j = 0; j < 18; j++) {
        const input = document.createElement('input');
        input.type = "button";
        input.name = "seats"
        input.classList = "seat";
        //3중포문을 사용하지 않기위해 
   
    mapping(input, i, j);
    div.append(input);
    $(document).on('click', '.seat', function () {
        var seatButton = $(this);
        var seatId = seatButton.data();
        
        // 이미 선택된 좌석이 2개 이상일 경우 추가 선택 막기
        if (selectedSeats.length >= 2 && !seatButton.hasClass('selected')) {
            alert("최대 2개의 좌석만 선택할 수 있습니다.");
            return; // 2개 이상 선택했을 때는 아무 작업도 하지 않음
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
            // 선택된 좌석 표시
            seatPicked1.innerText = selectedSeats[0] || ''; // 첫 번째 선택된 좌석
            seatPicked2.innerText = selectedSeats[1] || ''; // 두 번째 선택된 좌석
        }
    });
    
    }
}

function mapping(input, i, j) {
    if (i === 0) {
        input.value = "A" + j;
    } else if (i === 1) {
        input.value = "B" + j;
    } else if (i === 2) {
        input.value = "C" + j;
    } else if (i === 3) {
        input.value = "D" + j;
    } else if (i === 4) {
        input.value = "E" + j;
    } else if (i === 5) {
        input.value = "F" + j;
    } else if (i === 6) {
        input.value = "G" + j;
    } else if (i === 7) {
        input.value = "H" + j;
    } else if (i === 8) {
        input.value = "I" + j;
    } else if (i === 9) {
        input.value = "J" + j;
    } else if (i === 10) {
        input.value = "K" + j;
    } else if (i === 11) {
        input.value = "L" + j;
    }
}

// 입력값을 기준으로 class를 추가하는 함수
function setClassBasedOnValue(input) {
    // vip 클래스 조건
    if ((input.value >= "A4" && input.value <= "A15") ||
        (input.value >= "B4" && input.value <= "B15") ||
        (input.value >= "C4" && input.value <= "C15")) {
        input.classList.add("vip");
    }
    // r 클래스 조건
    else if (
        (input.value >= "A1" && input.value <= "A3") ||
        (input.value >= "A16" && input.value <= "A18") ||
        (input.value >= "B1" && input.value <= "B3") ||
        (input.value >= "B16" && input.value <= "B18") ||
        (input.value >= "C1" && input.value <= "C3") ||
        (input.value >= "C16" && input.value <= "C18") ||
        (input.value >= "D1" && input.value <= "D18") ||
        (input.value >= "E4" && input.value <= "E15") ||
        (input.value >= "F4" && input.value <= "F15") ||
        (input.value >= "G4" && input.value <= "G15") ||
        (input.value >= "H4" && input.value <= "H15")
    ) {
        input.classList.add("r");
    }
    // s 클래스 조건
    else if (
        (input.value >= "E1" && input.value <= "E3") ||
        (input.value >= "E16" && input.value <= "E18") ||
        (input.value >= "F1" && input.value <= "F3") ||
        (input.value >= "F16" && input.value <= "F18") ||
        (input.value >= "G1" && input.value <= "G3") ||
        (input.value >= "G16" && input.value <= "G18") ||
        (input.value >= "H1" && input.value <= "H3") ||
        (input.value >= "H16" && input.value <= "H18") ||
        (input.value >= "I1" && input.value <= "I18") ||
        (input.value >= "J4" && input.value <= "J15")
    ) {
        input.classList.add("s");
    }
    // 그 외 a 클래스
    else{
        input.classList.add("a")
    }
}
let input = document.querySelector('input');
setClassBasedOnValue(input);



$(document).ready(function(){

// 선택된 정보를 서버로 POST 요청
const form = $('<form></form>');
form.attr('method', 'POST');
form.attr('action', '/modam/reservConfirm');  // 정보를 전달할 서버 경로


// 선택한 좌석 정보를 폼에 추가
const selectedSeatsInput1 = $('<input>').attr('type', 'hidden').attr('name', 'selectedSeats1').val(selectedSeats[0]);
const selectedSeatsInput2 = $('<input>').attr('type', 'hidden').attr('name', 'selectedSeats2').val(selectedSeats[1]);

form.append(selectedSeatsInput1);
form.append(selectedSeatsInput2);

// 폼을 body에 추가하고 제출
$('body').append(form);
form.submit();
});