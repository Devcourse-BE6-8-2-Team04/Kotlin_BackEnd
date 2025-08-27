package com.team04.back.domain.review.review.dto

import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto

data class ReviewDto(
    val id: Int,
    val email: String,
    val imageUrl: String?,
    val title: String,
    val sentence: String,
    val tagString: String?,
    val weatherInfoDto: WeatherInfoDto
) {
    constructor(review: Review) : this(
        review.id,
        review.email,
        review.imageUrl,
        review.title,
        review.sentence,
        review.tagString,
        WeatherInfoDto(review.weatherInfo)
    )
}
