package com.arthall.modam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.arthall.modam.entity.PaymentsEntity;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Integer> {
    @Query("SELECT p.impUid FROM PaymentsEntity p WHERE p.reservation.id = :id")
    String findImpUidByResId(@Param("id") Integer id);
}
