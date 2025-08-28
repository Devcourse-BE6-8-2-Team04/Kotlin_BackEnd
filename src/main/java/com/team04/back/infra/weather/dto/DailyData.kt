package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 일 단위 날씨 데이터
 */
data class DailyData(
    val dt: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val moonrise: Long = 0,
    val moonset: Long = 0,
    @JsonProperty("moon_phase")
    val moonPhase: Double = 0.0,
    val summary: String? = null,
    val temp: DailyTemp? = null,
    @JsonProperty("feels_like")
    val feelsLike: DailyFeelsLike? = null,
    val pressure: Int = 0,
    val humidity: Int = 0,
    @JsonProperty("dew_point")
    val dewPoint: Double = 0.0,
    @JsonProperty("wind_speed")
    val windSpeed: Double = 0.0,
    @JsonProperty("wind_deg")
    val windDeg: Int = 0,
    @JsonProperty("wind_gust")
    val windGust: Double = 0.0,
    val weather: List<WeatherDescription> = emptyList(),
    val clouds: Int = 0,
    val pop: Double = 0.0,
    val rain: Double = 0.0,
    val snow: Double = 0.0,
    val uvi: Double = 0.0
)
