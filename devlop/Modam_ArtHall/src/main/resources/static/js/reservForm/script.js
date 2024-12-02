$(document).ready(function () {
    const calendarInput = $('#calendarInput');
    const selectDate = $('#showDate');
    const showTimeSelect = $('#showTime');
    const performanceIdField = $('#performanceId');
    const seatAvailableInput = $('#seatAvailableInput');
    const numberOfPeopleSelect = $('#numberOfPeople');
    let seatAvailable = 0; // 잔여석을 추적할 변수
    let gettedshowId = ''; // showId를 저장할 변수

    // 페이지가 처음 로드될 때 초기 값 설정
    const today = calendarInput.val();
    selectDate.val(today);

    // 날짜가 선택될 때마다 hidden input에 선택된 날짜 반영
    calendarInput.on('change', function () {
        const selectedDate = calendarInput.val();
        // 날짜 값이 "yyyy-MM-dd" 형식으로 설정되도록 변환
        const formattedDate = formatDateToYMD(selectedDate);
        selectDate.val(formattedDate);  // 날짜가 변환되어 showDate input에 설정
        fetchShowId(); // 날짜가 변경될 때마다 showId를 새로 가져오기
        console.log(formattedDate);
    });

    // 'showTime' 값이 변경될 때마다 폼을 업데이트
    showTimeSelect.on('change', function () {
        const selectedTime = $(this).val();
        updateForm(selectedTime, numberOfPeopleSelect.val());
        fetchShowId(); // 회차가 변경될 때마다 showId를 새로 가져오기
        console.log(selectedTime);
    });

    // 'numberOfPeople' 값이 변경될 때마다 폼을 업데이트
    numberOfPeopleSelect.on('change', function () {
        const numberOfPeople = $(this).val();
        if (numberOfPeople > seatAvailable) {
            alert('선택한 인원 수가 잔여석보다 많습니다.');
            $(this).val(seatAvailable); // 잔여석보다 큰 값을 선택하면 잔여석 값으로 되돌림
        } else {
            updateForm(showTimeSelect.val(), numberOfPeople);
        }
    });

    // 선택된 날짜와 회차를 기반으로 showId를 가져오는 함수
    function fetchShowId() {
        return new Promise((resolve, reject) => {
            const selectedDate = calendarInput.val();
            const selectedTime = showTimeSelect.val();
            const performanceId = performanceIdField.val();

            // 디버깅을 위한 상세 로그
            console.log("Performance ID Field:", performanceIdField);
            console.log("Performance ID Field Length:", performanceIdField.length);
            console.log("Performance ID Value:", performanceId);
            console.log("Selected Date:", selectedDate);
            console.log("Selected Time:", selectedTime);

            // 값 검증 전에 콘솔에 출력
            if (!performanceId) {
                console.log("Performance ID is missing");
            }

            if (!selectedDate) {
                console.log("Date is missing");
            }

            if (!selectedTime) {
                console.log("Time is missing");
            }

            // 날짜 검증 수정
            if (!selectedDate || selectedDate.trim() === '') {
                reject('날짜를 선택해주세요.');
                return;
            }

            if (!performanceId || performanceId.trim() === '') {
                reject('공연 정보를 찾을 수 없습니다.');
                return;
            }

            if (!selectedTime || selectedTime.trim() === '') {
                reject('회차를 선택해주세요.');
                return;
            }

            const date = new Date(selectedDate);
            const formattedDate = formatDateToYMD(date);
            console.log("Formatted Date for AJAX:", formattedDate);

            $.ajax({
                url: '/getShowId',
                method: 'GET',
                data: {
                    performanceId: performanceId,
                    showDate: formattedDate,
                    showTime: selectedTime
                },
                success: function (data) {
                    if (data.showId) {
                        gettedshowId = data.showId;
                        console.log('검색된 showId : ' + gettedshowId);
                        updateForm(selectedTime, numberOfPeopleSelect.val());
                        fetchSeatAvailability();
                        resolve(data);
                    } else {
                        reject('해당 회차의 공연 정보를 찾을 수 없습니다.');
                    }
                },
                error: function (xhr, status, error) {
                    console.error('AJAX 오류 상세정보:', {
                        status: xhr.status,
                        statusText: xhr.statusText,
                        responseText: xhr.responseText,
                        error: error
                    });
                    reject(error);
                }
            });
        });
    }

    // showId에 해당하는 공연의 seat_available 값을 가져오는 함수
    function fetchSeatAvailability() {
        if (!gettedshowId) return; // showId가 없으면 리턴

        $.ajax({
            url: '/getSeatAvailability',
            method: 'GET',
            data: { showId: gettedshowId },
            success: function (data) {
                if (data.seatAvailable !== undefined) {
                    seatAvailable = data.seatAvailable; // 잔여석 값을 변수에 저장
                    seatAvailableInput.text(seatAvailable); // 잔여석 정보 업데이트

                    // 잔여석이 0이면 numberOfPeople select를 비활성화
                    if (seatAvailable === 0) {
                        numberOfPeopleSelect.prop('disabled', true);
                    } else {
                        numberOfPeopleSelect.prop('disabled', false);
                    }

                    // 선택된 인원이 잔여석보다 많으면 alert 띄우기
                    const selectedPeople = numberOfPeopleSelect.val();
                    if (selectedPeople > seatAvailable) {
                        alert('선택한 인원 수가 잔여석보다 많습니다.');
                        numberOfPeopleSelect.val(seatAvailable); // 잔여석 값으로 선택되게 설정
                    }
                } else {
                    console.error("잘못된 응답 데이터:", data);
                seatAvailableInput.text('정보를 가져올 수 없습니다.');
                }
            },
            error: function (xhr, status, error) {
                console.error('잔여석 조회 AJAX 오류:', {
                    status: xhr.status,
                    statusText: xhr.statusText,
                    responseText: xhr.responseText,
                    error: error
                });
                seatAvailableInput.text('정보를 가져올 수 없습니다.');
            }
        });
    }
    // 날짜를 "yyyy-MM-dd" 형식으로 변환하는 함수
    function formatDateToYMD(dateStr) {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1
        const day = String(date.getDate()).padStart(2, '0');

        return `${year}-${month}-${day}`;
    }

    // 페이지가 처음 로드될 때 초기 폼 값 설정
    fetchShowId();

    // 선택된 회차와 인원 수를 폼에 추가하는 함수
    function updateForm(selectedTime, numberOfPeople) {
        const form = $("#formToseatSelect");
        
        console.log("Form before update:", form);  // 폼 엘리먼트 확인
        
        // 기존 input 필드들 제거
        $('input[name="showTime"]', form).remove();
        $('input[name="numberOfPeople"]', form).remove();
        $('input[name="showId"]', form).remove();

        // 디버깅을 위한 로그
        console.log("Updating form with:", {
            showId: gettedshowId,
            showTime: selectedTime,
            numberOfPeople: numberOfPeople
        });

        // 선택된 값으로 새로운 입력 필드 추가
        if (gettedshowId) {
            // 직접 HTML 문자열로 input 추가
            form.append(`
                <input type="hidden" name="showId" value="${gettedshowId}" />
                <input type="hidden" name="showTime" value="${selectedTime}" />
                <input type="hidden" name="numberOfPeople" value="${numberOfPeople}" />
            `);

            // 추가된 input 값들 확인
            console.log("Added inputs:", {
                showId: $('input[name="showId"]', form).val(),
                showTime: $('input[name="showTime"]', form).val(),
                numberOfPeople: $('input[name="numberOfPeople"]', form).val()
            });

            // 전체 폼 내용 확인
            console.log("Form HTML after update:", form.html());
        }
    }

    $('#toSeatSelect').on('click', function (e) {
        e.preventDefault(); // 기본 이벤트 방지
        console.log("showDate: " + $('#showDate').val());
        console.log("showTime: " + $('#showTime').val());
        console.log("numberOfPeople: " + $('#numberOfPeople').val());
        console.log("showId: " + $('#showId').val());
        
        const showTime = $('#showTime').val();
        const numberOfPeople = $('#numberOfPeople').val();
    
        if (!showTime || !numberOfPeople) {
            alert("회차와 인원수를 선택해주세요.");
            return;
        }
    
        // showId를 가져오는 함수를 실행하고, 성공 시 폼 제출
        fetchShowId().then(() => {
            if (gettedshowId) {
                updateForm(showTime, numberOfPeople);
                $("#formToseatSelect").submit();
            } else {
                alert("공연 회차 정보를 불러오지 못했습니다. 다시 시도해주세요.");
            }
        }).catch(error => {
            console.error('Error:', error);
            alert("공연 회차 정보를 불러오는 중 오류가 발생했습니다.");
        });
    });


});