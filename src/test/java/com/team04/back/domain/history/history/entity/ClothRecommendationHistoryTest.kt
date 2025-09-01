package com.team04.back.domain.history.history.entity

import com.team04.back.common.fixture.FixtureFactory
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.user.user.entity.User
import com.team04.back.domain.weather.weather.enums.Weather
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ClothRecommendationHistoryTest {

    @Test
    fun `ClothRecommendationHistory 생성 테스트`() {
        // given
        val user = User()
        val weatherInfos = listOf(
            FixtureFactory.createWeatherInfo("서울", LocalDate.now(), Weather.CLEAR_SKY, 22.0)
        )
        val liked: ClothInfo = FixtureFactory.createClothInfo(Style.OUTDOOR, 15.0, 25.0)
        val unLiked: ClothInfo = FixtureFactory.createClothInfo(Style.CASUAL_DAILY, 5.0, 15.0)

        // when
        val history = FixtureFactory.createClothRecommendationHistory(
            user,
            "서울",
            weatherInfos,
            listOf(liked),
            listOf(unLiked)
        )

        // then
        Assertions.assertThat(history.user).isEqualTo(user)
        Assertions.assertThat(history.location).isEqualTo("서울")
        Assertions.assertThat(history.weatherInfo).hasSize(1)
        Assertions.assertThat(history.likedClothings).contains(liked)
        Assertions.assertThat(history.unLikedClothings).contains(unLiked)
        Assertions.assertThat(history.feelsLike).isEqualTo(20.0)
        Assertions.assertThat(history.dailyTemperatureGap).isEqualTo(9.0)
    }
}
