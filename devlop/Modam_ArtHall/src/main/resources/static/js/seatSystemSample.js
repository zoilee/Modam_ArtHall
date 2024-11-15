//좌석 dto 및 그에 따른 class들을 만들지 않고 script로 처리
//dto 만드는건 상영작과 상영스케줄 정도
/*스케줄 dto에 들어간 것들
public class ScheduleDto {
	
	private long showScheduleNo;	//상영번호
	private int timeNo;	//상영회차
	private int showTypeNo;	//상영타입번호
	private int showTypeName;	//상영타입번호
	private Date startTime;	//시작시간
	private Date endTime;	//끝시간
}
    모담은 id, 날짜, 회차, 시작시간 정도만 들어가면 될 듯
*/
//뮤지컬 선택하면 뮤지컬 id, title 받아오기
//스케줄 dto에서 위의 내용 받아오기
//좌석은 script로 처리하고 좌석 id 받은 후 예약완료 처리

$("#show-seat").append(function(){    
    let noSeat = new Array();//빈 좌석
    <c:forEach var="emptys" items="${emptySeat }">
        noSeat.push("${emptys.no}")
    </c:forEach>
    let disabled = new Array();//예약마감좌석
    <c:forEach var="noEamptys" items="${noEamptySeat }">
        disabled.push("${noEamptys.no}");
    </c:forEach>
    let seatrow = $('div.ptag').attr('data-row');	//열의좌석 꺼내왔을 때 변수에 저장seatcolumn
    let seatcolumn = $('div.ptag').attr('data-column');		//행의좌석 꺼내왔을 때 변수에 저장seatrow
   
    let max = new Map();
      max.set("A",0);
      max.set("B",1);
      max.set("C",2);
      max.set("D",3);
      max.set("E",4);
      max.set("F",5);
      max.set("G",6);
      max.set("H",7);
      max.set("I",8);
      max.set("J",9);
      max.set("K",10);
      max.set("L",11);
      max.set("M",12);
      max.set("N",13);
      max.set("O",14);
      max.set("P",15);
      max.set("Q",16);
      max.set("R",17);
      max.set("S",18);
      max.set("T",19);
      max.set("U",20);
      max.set("V",21);
      max.set("W",22);
      max.set("X",23);
      max.set("Y",24);
      max.set("Z",25);
      
let count = 0;
let totalList = [];
const englishs =['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'];
                      
let resultNoSeatColumn = [];		//빈좌석 행에 대한 배열 초기화
let noseatRow = [];		//빈좌석 열에 대한 배열 초기화
   for(let i = 0; i < noSeat.length; i++) {
        resultNoSeatColumn.push(noSeat[i].slice(0,1)); //빈좌석 행을 나눔
        noseatRow.push(parseInt(noSeat[i].slice(1))); //빈좌석 행을 나눈것을 형변환
    }
    let resultDisabledColumn = [];	//장애인석 행에 대한 배열 초기화 나중에 예약완료석으로 변경예정
    let disabledRow = [];		//장애인석 열에 대한 배열 초기화 나중에 예약완료석으로 변경예정
     for(let i = 0; i < disabled.length;i++){
         resultDisabledColumn.push(disabled[i].slice(0,1)); //장애인석 행을 나눔
         disabledRow.push(parseInt(disabled[i].slice(1)));//숫자로 변환하는 메소드
       }
    
 let disabledColumn = [];
for(let i = 0; i<resultDisabledColumn.length;i++){
    disabledColumn.push(max.get(resultDisabledColumn[i]));
}		
 let noseatColumn = [];
for(let i = 0; i<resultNoSeatColumn.length;i++){
    noseatColumn.push(max.get(resultNoSeatColumn[i]));
}	

let resultRow = max.get(seatrow); 
})
//좌석에 값 넣기
$('div#show-seat').on("click","input[type=button].seat-condition-choise",function(){
    let $choiesSeat = $(this);
    let valueString = $choiesSeat.val();
    $choiesSeat.attr('class','seat-condition-common');
    $choiesSeat.css('color','white');
    $('div.seat-all:contains('+valueString+')').attr("구매가능좌석");
    $('div.seat-all:contains('+valueString+')').attr('class','seat-position').text('-');
    $('div.seat-all:contains('+valueString+')').text('-');
    $('input[name=seat-No'+totalSeat+']').val("");
    totalSeat--;
})
  function countCoulumn(x, y){//화면에 뿌릴 전체 값 메소드
     let countcolumns = [];
     for(let i = 0; i < x.length; i++) {
         countcolumns.push(y.findIndex((e) => e === x[i])); 
     }
     return countcolumns;
  }

  function mapping(input,i,j) { //변환하는 메소드
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
      } else if (i === 12) {
          input.value = "M" + j;
      } else if (i === 13) {
          input.value = "N" + j;
      } else if (i === 14) {
          input.value = "O" + j;
      } else if (i === 15) {
          input.value = "P" + j;
      } else if (i === 16) {
          input.value = "Q" + j;
      } else if (i === 17) {
          input.value = "R" + j;
      } else if (i === 18) {
          input.value = "S" + j;
      } else if (i === 19) {
          input.value = "T" + j;
      } else if (i === 20) {
          input.value = "U" + j;
      } else if (i === 21) {
          input.value = "V" + j;
      } else if (i === 22) {
          input.value = "W" + j;
      } else if (i === 23) {
          input.value = "X" + j;
      } else if (i === 24) {
          input.value = "Y" + j;
      } else if (i === 25) {
          input.value = "Z" + j;
      } 
    }


    //다른 스크립트
    let test = [];
    let selectedSeats = new Array();
    let selectedSeatsMap = [];
    const seatWrapper = document.querySelector(".seat-wrapper");
    let clicked = "";
    let div = "";

    for (let i = 0; i < 7; i++) {
        div = document.createElement("div");
        seatWrapper.append(div);
        for (let j = 0; j < 7; j++) {
            const input = document.createElement('input');
            input.type = "button";
            input.name = "seats"
            input.classList = "seat";
            //3중포문을 사용하지 않기위해 
            mapping(input, i, j);
            div.append(input);
            input.addEventListener('click', function(e) {
                console.log(e.target.value);
                //중복방지 함수
                selectedSeats = selectedSeats.filter((element, index) => selectedSeats.indexOf(element) != index);

                //click class가 존재할때(제거해주는 toggle)
                if (input.classList.contains("clicked")) {
                    input.classList.remove("clicked");
                    clicked = document.querySelectorAll(".clicked");
                    selectedSeats.splice(selectedSeats.indexOf(e.target.value), 1);
                    clicked.forEach((data) => {
                        selectedSeats.push(data.value);
                    });
                    //click class가 존재하지 않을때 (추가해주는 toggle)
                } else {
                    input.classList.add("clicked");
                    clicked = document.querySelectorAll(".clicked");
                    clicked.forEach((data) => {
                        selectedSeats.push(data.value);
                    })
                }
                console.log(selectedSeats);
            })
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
        }
    }

    