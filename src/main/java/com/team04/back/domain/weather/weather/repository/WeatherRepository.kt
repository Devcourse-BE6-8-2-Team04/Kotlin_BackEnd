package com.team04.back.domain.weather.weather.repository

import com.team04.back.domain.weather.weather.entity.WeatherInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface WeatherRepository : JpaRepository<WeatherInfo, Long> {
    fun findByLocationAndDateBetween(location: String, start: LocalDate, end: LocalDate): List<WeatherInfo>
    fun findByLocationAndDate(location: String, date: LocalDate): WeatherInfo?
}
