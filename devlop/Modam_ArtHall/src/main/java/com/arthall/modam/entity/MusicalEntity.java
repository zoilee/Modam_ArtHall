package com.arthall.modam.entity;

import java.util.List;

import com.arthall.modam.dto.ShowDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class MusicalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int musicalId;

    private String musicaltitle;
    private int musicalDuration;
    private int musicalAgeLimit;
    private int musicalVIPValue;
    private int musicalRValue;
    private int musicalSValue;
    private int musicalAValue;

    @OneToMany(mappedBy = "musical")
    private List<ShowEntity> shows;  // 여러 회차를 가질 수 있음

    // getters and setters
}
