package com.team04.back.domain.review.review.entity

import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Review(
    email: String,
    password: String,
    title: String,
    sentence: String,
    tagString: String?,
    imageUrl: String?,
    weatherInfo: WeatherInfo
) : BaseEntity() {
    @Column(nullable = false)
    val email: String = email

    @Column(nullable = false)
    val password: String = password

    @Column(length = 100, nullable = false)
    var title: String = title

    @Column(columnDefinition = "TEXT", nullable = false)
    var sentence: String = sentence

    var tagString: String? = tagString

    @Column(length = 2048)
    var imageUrl: String? = imageUrl

    @JoinColumn(name = "weather_info_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var weatherInfo: WeatherInfo = weatherInfo


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
