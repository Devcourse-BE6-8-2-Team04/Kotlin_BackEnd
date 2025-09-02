package com.team04.back.domain.history.history.entity

import com.team04.back.common.fixture.FixtureFactory
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
import com.team04.back.domain.weather.weather.enums.Weather
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ClothRecommendationHistoryTest {

    @Test
    fun `ClothRecommendationHistory 생성 테스트`() {
        // given
        val member = Member(
            userId = "testUser",
            password = "testPassword",
            email = "test@example.com",
            age = 30,
            gender = Gender.MALE,
            tendency = Tendency.NEUTRAL
        )

        // 파라미터 추가
        val weatherInfos = listOf(
            FixtureFactory.createWeatherInfo(
                "서울",
                LocalDate.now(),
                Weather.CLEAR_SKY,
                22.0,     // feelsLikeTemperature
                10.0,     // dailyTemperatureGap
                0.0,      // rain
                0.0,      // snow
                5.0,      // windSpeed
                3.0       // uvi
            )
        )
        val liked: ClothInfo = FixtureFactory.createClothInfo(Style.OUTDOOR, 15.0, 25.0)
        val unLiked: ClothInfo = FixtureFactory.createClothInfo(Style.CASUAL_DAILY, 5.0, 15.0)

        // when
        val history = FixtureFactory.createClothRecommendationHistory(
            member,
            "서울",
            weatherInfos,
            listOf(liked),
            listOf(unLiked)
        )

        // then
        Assertions.assertThat(history.member).isEqualTo(member)
        Assertions.assertThat(history.location).isEqualTo("서울")
        Assertions.assertThat(history.weatherInfo).hasSize(1)
        Assertions.assertThat(history.likedClothings).contains(liked)
        Assertions.assertThat(history.dislikedClothings).contains(unLiked)
        Assertions.assertThat(history.feelsLike).isEqualTo(20.0)
        Assertions.assertThat(history.dailyTemperatureGap).isEqualTo(9.0)
    }
}
