package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.weather.weather.entity.WeatherInfo

data class WeatherClothResponseDto(
    val weatherInfo: WeatherInfo,
    val recommendedOutfits: Map<Category, List<ClothInfo>>,
    val notRecommendedOutfits: Map<Category, List<ClothInfo>>
)