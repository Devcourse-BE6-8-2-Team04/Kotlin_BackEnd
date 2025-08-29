package com.team04.back.infra.weather.dto

/**
 * 날씨 상태 설명 데이터
 */
data class WeatherDescription(
    val id: Int = 0,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)
