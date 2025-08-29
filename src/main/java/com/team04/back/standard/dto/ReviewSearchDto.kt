package com.team04.back.standard.dto

import java.time.LocalDate

data class ReviewSearchDto(
    val location: String?,
    val date: LocalDate?,
    val feelsLikeTemperature: Double?,
    val month: Int?,
    val email: String?
) {}