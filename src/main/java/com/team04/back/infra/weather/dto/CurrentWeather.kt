package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CurrentWeather(
    val dt: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val temp: Double = 0.0,
    @JsonProperty("feels_like")
    val feelsLike: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0,
    @JsonProperty("dew_point")
    val dewPoint: Double = 0.0,
    val uvi: Double = 0.0,
    val clouds: Int = 0,
    val visibility: Int = 0,
    @JsonProperty("wind_speed")
    val windSpeed: Double = 0.0,
    @JsonProperty("wind_deg")
    val windDeg: Int = 0,
    @JsonProperty("wind_gust")
    val windGust: Double = 0.0,
    val weather: List<WeatherDescription> = emptyList()
)
