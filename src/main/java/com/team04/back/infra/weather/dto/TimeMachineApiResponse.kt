package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 과거(Time Machine) 날씨 API 응답 DTO
 */
data class TimeMachineApiResponse(

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
     * 타임머신 날씨 데이터 배열
     */
    val data: List<TimeMachineData> = emptyList()
)
