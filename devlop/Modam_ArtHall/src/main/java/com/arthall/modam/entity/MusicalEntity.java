package com.arthall.modam.entity;

import java.util.List;

import com.arthall.modam.dto.ShowDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public MusicalEntity(String musicalTitle, int musicalDuration, int musicalAgeLimit, int musicalVIPValue, int musicalRValue, int musicalSValue, int musicalAValue) {
    this.musicalTitle = musicalTitle;
    this.musicalDuration = musicalDuration;
    this.musicalAgeLimit = musicalAgeLimit;
    this.musicalVIPValue = musicalVIPValue;
    this.musicalRValue = musicalRValue;
    this.musicalSValue = musicalSValue;
    this.musicalAValue = musicalAValue;

    @OneToMany(mappedBy = "musical")
    private List<ShowEntity> shows;  // 여러 회차를 가질 수 있음

    // getters and setters
}
