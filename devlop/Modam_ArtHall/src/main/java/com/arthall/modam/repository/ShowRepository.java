package com.arthall.modam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arthall.modam.dto.ShowDto;

@Repository
public interface ShowRepository extends JpaRepository<ShowEntity, Integer> {
    ShowEntity findByMusicalAndShowDateAndShowTime(MusicalEntity musical, LocalDate showDate, int showTime);
}

