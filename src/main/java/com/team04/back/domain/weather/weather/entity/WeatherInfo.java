package com.team04.back.domain.weather.weather.entity;

import com.team04.back.domain.weather.weather.enums.Weather;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class WeatherInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 날씨 (enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Weather weather;

    // 날씨 상태 요약 (한국어)
    @Column
    private String description;

    // 일교차
    @Column(nullable = false)
    private Double dailyTemperatureGap;

    // 체감 온도
    @Column(nullable = false)
    private Double feelsLikeTemperature;

    // 최고 온도
    @Column(nullable = false)
    private Double maxTemperature;

    // 최저 온도
    @Column(nullable = false)
    private Double minTemperature;

    // 강수 확률 (0.0 ~ 1.0)
    @Column
    private Double pop;

    // 강수량 (mm)
    @Column
    private Double rain;

    // 적설량 (mm)
    @Column
    private Double snow;

    // 습도 (0~100%)
    @Column
    private Integer humidity;

    // 풍속 (m/s)
    @Column
    private Double windSpeed;

    // 풍향 (0~360°, 북: 0, 동: 90, 남: 180, 서: 270)
    @Column
    private Integer windDeg;

    // 자외선 지수
    @Column
    private Double uvi;

    // 지역 (지역구 기준)
    @Column(nullable = false)
    private String location;

    // 날짜
    @Column(nullable = false)
    private LocalDate date;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    public WeatherInfo(
            Weather weather,
            Double dailyTemperatureGap,
            Double feelsLikeTemperature,
            Double maxTemperature,
            Double minTemperature,
            String location,
            LocalDate date
    ) {
        this.weather = weather;
        this.dailyTemperatureGap = dailyTemperatureGap;
        this.feelsLikeTemperature = feelsLikeTemperature;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.location = location;
        this.date = date;
    }
}
