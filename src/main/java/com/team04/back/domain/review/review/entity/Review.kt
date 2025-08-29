package com.team04.back.domain.review.review.entity

import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Review(
    val email: String,
    val password: String,
    @field:Column(length = 100) var title: String,
    @field:Column(columnDefinition = "TEXT") var sentence: String,
    var tagString: String?,
    @field:Column(length = 2048) var imageUrl: String?,

    @field:JoinColumn(name = "weather_info_id")
    @field:ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var weatherInfo: WeatherInfo
) : BaseEntity() {
    fun modify(
        title: String,
        sentence: String,
        tagString: String?,
        imageUrl: String?,
        weatherInfo: WeatherInfo
    ): Review {
        this.title = title
        this.sentence = sentence
        this.tagString = tagString
        this.imageUrl = imageUrl
        this.weatherInfo = weatherInfo

        return this
    }
}
