package com.team04.back.domain.history.history.entity

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.user.user.entity.User
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class ClothRecommendationHistory(
    // 사용자
    // FIXME
    // 식별자가 필요한데 사용자 도메인이 개발되지 않아
    // @Entity
    // public class User extends BaseEntity {}
    // 로 임시 생성하여 사용하였습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    // 지역
    @Column(name = "location", nullable = false)
    val location: String,

    //weatherInfo List (날씨 정보)
    @OneToMany(fetch = FetchType.LAZY)
    val weatherInfo: List<WeatherInfo> = mutableListOf(),

    //추천 의류 리스트
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "history_liked_clothing_map",
        joinColumns = [JoinColumn(name = "history_id")],
        inverseJoinColumns = [JoinColumn(name = "clothing_id")]
    )
    val likedClothings: List<ClothInfo> = mutableListOf(),

    //비추천 의류 리스트
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "history_unliked_clothing_map",
        joinColumns = [JoinColumn(name = "history_id")],
        inverseJoinColumns = [JoinColumn(name = "clothing_id")]
    )
    val unLikedClothings: List<ClothInfo> = mutableListOf(),

    // 체감온도
    @Column(name = "feels_like")
    val feelsLike: Double,

    // UVI
    @Column(name = "uvi")
    val uvi: Double,

    // 강수량
    @Column(name = "rain")
    val rain: Double,

    // 적설량
    @Column(name = "snow")
    val snow: Double,

    // 습도
    @Column(name = "humidity")
    val humidity: Int,

    // 풍속
    @Column(name = "wind_speed")
    val windSpeed: Double,

    // 최저기온
    @Column(name = "temp_min")
    val tempMin: Double,

    // 최고기온
    @Column(name = "temp_max")
    val tempMax: Double,

    @Column(name = "temp_gap")
    val dailyTemperatureGap: Double,

    //리뷰 작성 시간
    @Column(name = "reviewed_at")
    var reviewedAt: LocalDateTime
) : BaseEntity() {

}