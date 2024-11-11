package com.arthall.modam.entity;

import jakarta.persistence.Entity;

@Entity
public class SeatSelectEntity {
    int selectSeat(int seatId);
    int disSelectAll(int seatId);
}
