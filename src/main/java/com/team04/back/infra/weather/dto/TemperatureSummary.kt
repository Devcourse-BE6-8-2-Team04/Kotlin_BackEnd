package com.team04.back.infra.weather.dto

data class TemperatureSummary(
    val min: Double = 0.0,
    val max: Double = 0.0,
    val afternoon: Double = 0.0,
    val night: Double = 0.0,
    val evening: Double = 0.0,
    val morning: Double = 0.0
)
