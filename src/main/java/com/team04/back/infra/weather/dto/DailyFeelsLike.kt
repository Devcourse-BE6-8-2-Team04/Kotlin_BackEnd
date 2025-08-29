package com.team04.back.infra.weather.dto

/**
 * 체감 온도 데이터
 */
data class DailyFeelsLike(
    val day: Double = 0.0,
    val night: Double = 0.0,
    val eve: Double = 0.0,
    val morn: Double = 0.0
)
