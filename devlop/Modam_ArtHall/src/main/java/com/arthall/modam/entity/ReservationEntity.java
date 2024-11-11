package com.arthall.modam.entity;

import java.util.List;

import com.arthall.modam.dto.ReservationDto;

import jakarta.persistence.Entity;

@Entity
public class ReservationEntity {
    List<ReservationDto> getAllRes();
    ReservationDto getRes(int id);
    void insertRes(ReservationDto dto);
    void updateRes(ReservationDto dto);
    void delRes(int id);
}
