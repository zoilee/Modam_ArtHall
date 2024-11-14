let selectedSeats = new Array();
let selectedSeatsClass = new Array();
const seatWrapper = document.querySelector(".seat-wrapper");
const seatPicked = document.querySelector("#seatPicked");
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
        if (seatButton.hasClass('selected')) {
            seatButton.removeClass('selected');
            selectedSeats = selectedSeats.filter(function (id) {
                return id !== seatId;
            });
        } else {
            seatButton.addClass('selected');
            selectedSeats.push(seatId);
            seatPicked.append(seatId);
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

$(document).ready(function(){
// 선택된 정보를 서버로 POST 요청
const form = $('<form></form>');
form.attr('method', 'POST');
form.attr('action', '/modam/reservConfirm');  // 정보를 전달할 서버 경로

// 선택된 musical ID를 폼에 추가
const musicalIdInput = $('<input>').attr('type', 'hidden').attr('name', 'musicalId').val(musicalId);
form.append(musicalIdInput);

// 선택한 날짜, 회차, 인원, 좌석 정보를 폼에 추가
const selectedDateInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedDate').val(selected_date);
const selectedTimeInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedTime').val(selected_time);
const numberOfPeopleInput = $('<input>').attr('type', 'hidden').attr('name', 'numberOfPeople').val(numberOfPeople);
const selectedSeatsInput = $('<input>').attr('type', 'hidden').attr('name', 'selectedSeats').val(selectedSeats);

form.append(selectedDateInput);
form.append(selectedTimeInput);
form.append(numberOfPeopleInput);
form.append(selectedSeatsInput);

// 폼을 body에 추가하고 제출
$('body').append(form);
form.submit();
});

