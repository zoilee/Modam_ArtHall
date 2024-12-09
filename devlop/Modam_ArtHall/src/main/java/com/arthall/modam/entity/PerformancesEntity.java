package com.arthall.modam.entity;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;




@Entity
@Table(name = "performances")
@Data
public class PerformancesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date") // 데이터베이스 컬럼과 매핑
    private Date startDate;

    @Column(name = "end_date") // 데이터베이스 컬럼과 매핑
    private Date endDate;

    @Column
    private int time;

    @Column(length = 100)
    private String location;

    @Column
    private int age;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "reference_id", referencedColumnName = "id", foreignKey = @jakarta.persistence.ForeignKey(value = jakarta.persistence.ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("reference_type = 'PERFORMANCE'")
    private List<ImagesEntity> imagesEntities;

    @OneToMany(mappedBy = "performancesEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude // toString()에서 제외
    private List<ShowEntity> shows;

    @Transient
    private String formattedAverageRating;

    @Transient
    private String formattedStartDate;

    @Transient
    private String formattedEndDate;

    // 필드 값 설정 메서드 (뷰에 전달할 값 포맷팅)
    public void setFormattedStartDate(String formattedStartDate) {
        this.formattedStartDate = formattedStartDate;
    }

    public void setFormattedEndDate(String formattedEndDate) {
        this.formattedEndDate = formattedEndDate;
    }

    public void setFormattedAverageRating(String formattedAverageRating) {
        this.formattedAverageRating = formattedAverageRating;
    }

}
