package com.arthall.modam.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class ReservationDto {
    private long reservation_id;
	private int user_id;
	private int seat_id;
    private int show_id;
	private Date selected_date;
	private int selected_time;
	private int numberOfPeople;
	private BigDecimal total_price;
	private Timestamp created_at;
	private List<SeatDto> reservedSeats;
}
