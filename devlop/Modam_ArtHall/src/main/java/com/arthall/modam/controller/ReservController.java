import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthall.modam.entity.PerformanceEntity;
import com.arthall.modam.entity.ShowEntity;
import com.arthall.modam.repository.MusicalRepository;
import com.arthall.modam.repository.ShowRepository;

@RestController
@RequestMapping("/reserve")
public class ReservController {

    @Autowired
    private MusicalRepository musicalRepository;

    @Autowired
    private ShowRepository showRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> reserve(@RequestBody Map<String, String> reservationData) {
        String selectedDate = reservationData.get("selectedDate");
        String selectedTime = reservationData.get("selectedTime");
        int numberOfPeople = Integer.parseInt(reservationData.get("numberOfPeople"));

        // 공연 날짜와 시간에 해당하는 ShowEntity 찾기
        LocalDate date = LocalDate.parse(selectedDate);
        int time = Integer.parseInt(selectedTime.equals("13:00") ? "1" : "2");  // 시간은 1 또는 2로 변환

        // 예시: "뮤지컬 제목"에 해당하는 뮤지컬 찾기 (필요에 따라 수정)
        PerformanceEntity musical = musicalRepository.findByPerformanceTitle("뮤지컬 제목");

        // 해당 날짜와 시간에 맞는 쇼 회차 찾기
        ShowEntity show = showRepository.findByMusicalAndShowDateAndShowTime(performanceId, date, time);

        if (show == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "해당 회차가 없습니다."));
        }

        // 예약 가능한 좌석 수 확인
        if (show.getSeatLimit() < numberOfPeople) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "좌석 수가 부족합니다."));
        }

        // 예약 처리 (예시로 ShowEntity의 좌석 수를 감소시키는 방식)
        show.setSeatLimit(show.getSeatLimit() - numberOfPeople);
        showRepository.save(show);

        // 예약 완료 응답
        return ResponseEntity.ok(Map.of("success", true, "message", "예약이 완료되었습니다."));
    }
}
