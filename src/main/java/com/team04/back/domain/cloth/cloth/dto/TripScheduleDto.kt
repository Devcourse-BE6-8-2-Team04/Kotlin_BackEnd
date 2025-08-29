package com.team04.back.domain.cloth.cloth.dto

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class TripScheduleDto(
    val start: @FutureOrPresent LocalDate?,
    val end: @FutureOrPresent LocalDate?,
    val lat: @Max(90) @Min(-90) Double,
    val lon: @Max(180) @Min(-180) Double
) {
    init {
        if (start != null && end != null) {
            require(start.isBefore(end)) { "시작 날짜는 종료 날짜보다 이전이어야 합니다." }
            require(ChronoUnit.DAYS.between(start, end) <= 30) { "최대 30일까지 조회할 수 있습니다." }
        }
    }
}
