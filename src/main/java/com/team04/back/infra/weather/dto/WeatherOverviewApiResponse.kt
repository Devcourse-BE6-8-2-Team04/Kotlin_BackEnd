package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WeatherOverviewApiResponse(

    /**
     * 위도, 십진수 (-90; 90)
     */
    val lat: Double = 0.0,

    /**
     * 경도, 십진수 (-180; 180)
     */
    val lon: Double = 0.0,

    /**
     * 시간대 (±XX:XX 형식)
     */
    val tz: String? = null,

    /**
     * 요약이 생성된 날짜 (YYYY-MM-DD 형식)
     */
    val date: String? = null,

    /**
     * 요청에 지정된 측정 단위
     */
    val units: String? = null,

    /**
     * AI가 생성한 요청된 날짜의 날씨 개요
     */
    @JsonProperty("weather_overview")
    val weatherOverview: String? = null
)
