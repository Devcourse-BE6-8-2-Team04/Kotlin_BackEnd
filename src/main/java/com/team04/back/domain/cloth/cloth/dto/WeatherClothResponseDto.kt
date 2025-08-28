package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.weather.weather.dto.WeatherInfoDto

@JvmRecord
data class WeatherClothResponseDto(
    @JvmField val weatherInfo: WeatherInfoDto,
    @JvmField val clothList: List<CategoryClothDto>,
    @JvmField val extraCloth: Set<ExtraClothDto>
) 