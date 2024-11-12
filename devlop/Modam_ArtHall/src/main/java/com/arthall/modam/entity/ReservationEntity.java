package com.arthall.modam.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.arthall.modam.dto.ReservationDto;
import com.arthall.modam.dto.SeatDto;
import com.arthall.modam.dto.ShowDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "show_id")
    private ShowDto show;

    private int user_id;
    private Date selected_date;
    private int selected_time;
    private int numberOfPeople;
    private BigDecimal total_price;
	private Timestamp created_at;

    @ManyToMany
    @JoinTable(
        name = "reservation_seat",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<SeatDto> reservedSeats;  // 예약된 좌석 리스트

    // getters and setters
}
