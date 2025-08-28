package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OpenWeather One Call API 응답 DTO
 */
data class OneCallApiResponse(

    /**
     * 위도, 십진수 (-90; 90)
     */
    val lat: Double = 0.0,

    /**
     * 경도, 십진수 (-180; 180)
     */
    val lon: Double = 0.0,

    /**
     * 요청된 위치의 시간대 이름
     */
    val timezone: String? = null,

    /**
     * UTC로부터의 시간 오프셋 (초)
     */
    @JsonProperty("timezone_offset")
    val timezoneOffset: Int = 0,

    /**
     * 현재 날씨 데이터
     */
    val current: CurrentWeather? = null,

    /**
     * 1시간 동안의 분 단위 예보 데이터
     */
    val minutely: List<MinutelyData> = emptyList(),

    /**
     * 48시간 동안의 시간 단위 예보 데이터
     */
    val hourly: List<HourlyData> = emptyList(),

    /**
     * 8일 동안의 일 단위 예보 데이터
     */
    val daily: List<DailyData> = emptyList(),

    /**
     * 국가 날씨 경보 데이터
     */
    val alerts: List<AlertData> = emptyList()
)
