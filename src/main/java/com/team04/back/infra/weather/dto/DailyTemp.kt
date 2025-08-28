package com.team04.back.infra.weather.dto

/**
 * 일 단위 온도 데이터
 */
data class DailyTemp(
    val day: Double = 0.0,
    val min: Double = 0.0,
    val max: Double = 0.0,
    val night: Double = 0.0,
    val eve: Double = 0.0,
    val morn: Double = 0.0
)
