package com.team04.back.domain.review.review.dto

import com.team04.back.domain.cloth.cloth.dto.CategoryClothDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto

data class ReviewDetailDto(
    val id: Int?,
    val memberId: Int?,
    val email: String?,
    val imageUrl: String?,
    val title: String,
    val sentence: String,
    val tagString: String?,
    val weatherInfoDto: WeatherInfoDto,
    val recommendedClothList: List<CategoryClothDto>,
    val nonRecommendedClothList: List<CategoryClothDto>
) {
    companion object {
        fun from(
            review: Review,
            recommendedClothList: List<ClothInfo>,
            nonRecommendedClothList: List<ClothInfo>
        ): ReviewDetailDto {
            return ReviewDetailDto(
                review.id,
                review.member?.id,
                review.email,
                review.imageUrl,
                review.title,
                review.sentence,
                review.tagString,
                WeatherInfoDto(review.weatherInfo),
                recommendedClothList.map { CategoryClothDto.from(it) },
                nonRecommendedClothList.map { CategoryClothDto.from(it) }
            )
        }
    }
}
