package com.arthall.modam.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.arthall.modam.entity.PerformancesEntity;
import com.arthall.modam.entity.ReservationsEntity;

import lombok.Data;

@Data
public class ReservationsDto {

    private int id;
    private int userId;
    private int showId;
    private String seatId1;
    private String seatId2;

    private Timestamp reservationDate;
    private BigDecimal totalPrice;
    private String status;

    public static ReservationsDto toReservationDto(ReservationsEntity reservationEntity) {
        ReservationsDto dto = new ReservationsDto();
        dto.setId(reservationEntity.getId());
        dto.setUserId(reservationEntity.getUserId());
        dto.setShowId(reservationEntity.getShowId());
        dto.setSeatId1(reservationEntity.getSeatId1());
        dto.setSeatId2(reservationEntity.getSeatId2());
        dto.setTotalPrice(reservationEntity.getTotalPrice());
        dto.setStatus(reservationEntity.getStatus());

        return dto;
    }

}
