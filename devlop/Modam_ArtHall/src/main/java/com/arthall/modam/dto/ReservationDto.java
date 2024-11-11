package com.arthall.modam.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import com.arthall.modam.entity.UserEntity;

import lombok.Data;

@Data
public class ReservationDto {

	@Autowired
	UserEntity userEntity = new UserEntity();

	@Autowired
	ShowDto showDto = new ShowDto();

	@Autowired
	SeatDto seatDto = new SeatDto();

    private int reservation_id;
	private int user_id;
	private int seat_id;
    private int show_id;
	private Date selected_date;
	private BigDecimal total_price;
	private Timestamp created_at;

	user_id = userEntity.getId();
}
