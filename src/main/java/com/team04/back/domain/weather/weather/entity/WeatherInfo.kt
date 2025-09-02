package com.team04.back.domain.weather.weather.entity

import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.global.jpa.entity.BaseEntity
import com.team04.back.infra.weather.dto.DailyData
import com.team04.back.infra.weather.dto.TimeMachineData
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

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

    // 3시간 이내에 수정된 데이터인지 확인
    fun isValid(): Boolean {
        return modifyDate.isAfter(LocalDateTime.now().minusHours(3))
    }

    // DailyData를 WeatherInfo로 매핑
    fun updateFromDailyData(data: DailyData, location: String, date: LocalDate) {
        this.weather = Weather.fromCode(data.weather.first().id)
        this.description = this.weather.description
        this.dailyTemperatureGap = (data.temp?.max ?: 0.0) - (data.temp?.min ?: 0.0)
        this.feelsLikeTemperature = data.feelsLike?.day ?: 0.0
        this.maxTemperature = data.temp?.max ?: 0.0
        this.minTemperature = data.temp?.min ?: 0.0
        this.location = location
        this.date = date
        this.pop = data.pop
        this.rain = data.rain
        this.snow = data.snow
        this.humidity = data.humidity
        this.windSpeed = data.windSpeed
        this.windDeg = data.windDeg
        this.uvi = data.uvi
    }

    // TimeMachineData를 WeatherInfo로 매핑
    fun updateFromTimeMachineData(data: TimeMachineData, location: String, date: LocalDate, minTemp: Double, maxTemp: Double) {
        val weather = Weather.fromCode(data.weather.first().id)
        val pop = if ((weather.code in 200 until 400) || (weather.code in 500 until 700)) 1.0 else 0.0

        this.weather = weather
        this.description = data.weather.first().description
        this.dailyTemperatureGap = maxTemp - minTemp
        this.feelsLikeTemperature = data.feelsLike
        this.maxTemperature = maxTemp
        this.minTemperature = minTemp
        this.location = location
        this.date = date
        this.pop = pop
        this.humidity = data.humidity
        this.windSpeed = data.windSpeed
        this.windDeg = data.windDeg
        this.uvi = data.uvi
    }
}
