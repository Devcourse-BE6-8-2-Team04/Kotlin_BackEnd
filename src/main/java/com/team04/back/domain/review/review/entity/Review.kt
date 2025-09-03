package com.team04.back.domain.review.review.entity

import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Review(
    member: Member? = null,
    email: String?,
    password: String?,
    title: String,
    sentence: String,
    tagString: String?,
    imageUrl: String?,
    weatherInfo: WeatherInfo
) : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member? = member  // 회원일 경우

    val email: String? = email  // 비회원일 경우

    val password: String? = password  // 비회원일 경우

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
        title: String?,
        sentence: String?,
        tagString: String?,
        imageUrl: String?,
        weatherInfo: WeatherInfo?
    ): Review {
        this.title = title ?: this.title
        this.sentence = sentence ?: this.sentence
        this.tagString = tagString ?: this.tagString
        this.imageUrl = imageUrl ?: this.imageUrl
        this.weatherInfo = weatherInfo ?: this.weatherInfo

        return this
    }
}
