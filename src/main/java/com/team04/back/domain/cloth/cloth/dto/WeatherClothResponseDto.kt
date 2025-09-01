package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.weather.weather.dto.WeatherInfoDto

data class WeatherClothResponseDto(
    val weatherInfo: WeatherInfoDto,
    val clothList: List<CategoryClothDto>,
)
