package com.team04.back.domain.weather.weather.entity

import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "weather_info")
class WeatherInfo(

    // 날씨 (enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var weather: Weather,

    // 일교차
    @Column(nullable = false)
    var dailyTemperatureGap: Double,

    // 체감 온도
    @Column(nullable = false)
    var feelsLikeTemperature: Double,

    // 최고 온도
    @Column(nullable = false)
    var maxTemperature: Double,

    // 최저 온도
    @Column(nullable = false)
    var minTemperature: Double,

    // 지역 (지역구 기준)
    @Column(nullable = false)
    var location: String,

    // 날짜
    @Column(nullable = false)
    var date: LocalDate,

    // 날씨 상태 요약 (한국어)
    @Column
    var description: String? = null,

    // 강수 확률 (0.0 ~ 1.0)
    @Column
    var pop: Double? = null,

    // 강수량 (mm)
    @Column
    var rain: Double? = null,

    // 적설량 (mm)
    @Column
    var snow: Double? = null,

    // 습도 (0~100%)
    @Column
    var humidity: Int? = null,

    // 풍속 (m/s)
    @Column
    var windSpeed: Double? = null,

    // 풍향 (0~360°, 북: 0, 동: 90, 남: 180, 서: 270)
    @Column
    var windDeg: Int? = null,

    // 자외선 지수
    @Column
    var uvi: Double? = null

) : BaseEntity() {

    constructor() : this(
        weather = Weather.CLEAR_SKY,
        dailyTemperatureGap = 0.0,
        feelsLikeTemperature = 0.0,
        maxTemperature = 0.0,
        minTemperature = 0.0,
        location = "",
        date = LocalDate.now()
    )
}
