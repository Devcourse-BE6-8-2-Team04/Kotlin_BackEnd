package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DaySummaryApiResponse(
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
     * API 요청에 지정된 날짜 (YYYY-MM-DD 형식)
     */
    val date: String? = null,

    /**
     * 요청에 지정된 측정 단위
     */
    val units: String? = null,

    /**
     * 구름 관련 정보
     */
    @JsonProperty("cloud_cover")
    val cloudCover: CloudCover? = null,

    /**
     * 습도 관련 정보
     */
    val humidity: HumiditySummary? = null,

    /**
     * 강수량 관련 정보
     */
    val precipitation: PrecipitationSummary? = null,

    /**
     * 온도 관련 정보
     */
    val temperature: TemperatureSummary? = null,

    /**
     * 기압 관련 정보
     */
    val pressure: PressureSummary? = null,

    /**
     * 바람 관련 정보
     */
    val wind: WindSummary? = null
)
