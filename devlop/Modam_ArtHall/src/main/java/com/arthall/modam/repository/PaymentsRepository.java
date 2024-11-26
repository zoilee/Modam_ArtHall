package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.PaymentsEntity;
import com.arthall.modam.entity.ReservationsEntity;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Integer> {
    PaymentsEntity findByReservation(ReservationsEntity reservation);
}
