package com.team04.back.domain.review.review.dto

import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto

data class ReviewDto(
    val id: Int?,
    val email: String,
    val imageUrl: String?,
    val title: String,
    val weatherInfoDto: WeatherInfoDto
) {
    companion object {
        fun from(review: Review): ReviewDto {
            return ReviewDto(
                review.id,
                review.email,
                review.imageUrl,
                review.title,
                WeatherInfoDto(review.weatherInfo)
            )
        }
    }
}
