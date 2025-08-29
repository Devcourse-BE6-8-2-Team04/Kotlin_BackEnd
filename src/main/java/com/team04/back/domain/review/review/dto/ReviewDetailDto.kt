package com.team04.back.domain.review.review.dto

import com.team04.back.domain.cloth.cloth.dto.CategoryClothDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto

data class ReviewDetailDto(
    val id: Int,
    val email: String,
    val imageUrl: String?,
    val title: String,
    val sentence: String,
    val tagString: String?,
    val weatherInfoDto: WeatherInfoDto,
    val recommendedClothList: List<CategoryClothDto>,
    val nonRecommendedClothList: List<CategoryClothDto>
) {
    constructor(
        review: Review,
        recommendedClothList: List<ClothInfo>,
        nonRecommendedClothList: List<ClothInfo>
    ) : this(
        review.id,
        review.email,
        review.imageUrl,
        review.title,
        review.sentence,
        review.tagString,
        WeatherInfoDto(review.weatherInfo),
        recommendedClothList.map { CategoryClothDto(it) },
        nonRecommendedClothList.map { CategoryClothDto(it) }
    )
}
