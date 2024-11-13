let selectedSeats = new Array();
let selectedSeatsMap = [];
const seatWrapper = document.querySelector(".seat-wrapper");
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

