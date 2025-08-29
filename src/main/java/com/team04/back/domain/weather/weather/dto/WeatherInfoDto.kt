package com.team04.back.domain.weather.weather.dto

import com.team04.back.domain.weather.weather.entity.WeatherInfo
import java.time.LocalDate

data class WeatherInfoDto(
    val id: Int,
    val weather: String,
    val weatherCode: Int,
    val weatherDescription: String?,
    val dailyTemperatureGap: Double,
    val feelsLikeTemperature: Double,
    val maxTemperature: Double,
    val minTemperature: Double,
    val pop: Double?,
    val rain: Double?,
    val snow: Double?,
    val humidity: Int?,
    val windSpeed: Double?,
    val windDeg: Int?,
    val uvi: Double?,
    val location: String,
    val date: LocalDate
) {
    constructor(weatherInfo: WeatherInfo) : this(
        weatherInfo.id,
        weatherInfo.weather.name,
        weatherInfo.weather.code,
        weatherInfo.description,
        weatherInfo.dailyTemperatureGap,
        weatherInfo.feelsLikeTemperature,
        weatherInfo.maxTemperature,
        weatherInfo.minTemperature,
        weatherInfo.pop,
        weatherInfo.rain,
        weatherInfo.snow,
        weatherInfo.humidity,
        weatherInfo.windSpeed,
        weatherInfo.windDeg,
        weatherInfo.uvi,
        weatherInfo.location,
        weatherInfo.date
    )
}