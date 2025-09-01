package com.team04.back.domain.cloth.cloth.service

import com.team04.back.common.fixture.FixtureFactory
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import com.team04.back.domain.user.user.entity.User
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class ClothServiceTest {

    @InjectMocks
    lateinit var clothService: ClothService

    @Mock
    lateinit var clothRepository: com.team04.back.domain.cloth.cloth.repository.ClothRepository

    @Mock
    lateinit var clothRecommendationHistoryRepository: com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository

    @Test
    @DisplayName("기간(날씨 정보 리스트)이 주어지면, 각 날씨에 적절한 의류를 추천하여 카테고리별로 반환한다.")
    fun `get outfit recommendations with weather plan`() {
        // given
        val user = User()

        // 1. 날씨 계획
        val hotWeather = FixtureFactory.createWeatherInfo("Seoul", LocalDate.now().plusDays(1), Weather.CLEAR_SKY, 26.0)
        val mildWeather =
            FixtureFactory.createWeatherInfo("Seoul", LocalDate.now().plusDays(2), Weather.OVERCAST_CLOUDS, 16.0)
        val coldWeather =
            FixtureFactory.createWeatherInfo("Seoul", LocalDate.now().plusDays(3), Weather.HEAVY_RAIN, 4.0)
        val weatherPlan = listOf(hotWeather, mildWeather, coldWeather)

        // 2. 의류 생성
        val summerTee = FixtureFactory.createClothInfo(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, 20.0, 30.0)
        val shorts = FixtureFactory.createClothInfo(ClothName.SHORTS, Category.BOTTOM, Style.CASUAL_DAILY, 20.0, 30.0)
        val springJacket = FixtureFactory.createClothInfo(ClothName.JACKET, Category.EXTRA, Style.CASUAL_DAILY, 15.0, 25.0)
        val jeans = FixtureFactory.createClothInfo(ClothName.JEANS, Category.BOTTOM, Style.CASUAL_DAILY, 10.0, 20.0)
        val winterCoat = FixtureFactory.createClothInfo(ClothName.COAT, Category.EXTRA, Style.CASUAL_DAILY, 0.0, 10.0)
        val scarf = FixtureFactory.createClothInfo(ClothName.SCARF, Category.EXTRA, Style.OUTDOOR, 0.0, 10.0)
        val formalShirt = FixtureFactory.createClothInfo(ClothName.SHIRT, Category.TOP, Style.FORMAL_OFFICE, 10.0, 20.0)
        val runningShoes = FixtureFactory.createClothInfo(ClothName.ATHLETIC_SHOES, Category.SHOES, Style.OUTDOOR, 18.0, 28.0)

        // 3. 과거 추천 기록 mocking
        val hotHistory: ClothRecommendationHistory = FixtureFactory.createClothRecommendationHistory(
            user,
            "Seoul",
            listOf(hotWeather),
            listOf(summerTee, shorts, runningShoes),
            emptyList()
        )

        val mildHistory: ClothRecommendationHistory = FixtureFactory.createClothRecommendationHistory(
            user,
            "Seoul",
            listOf(mildWeather),
            listOf(springJacket, jeans, formalShirt),
            emptyList()
        )

        val coldHistory: ClothRecommendationHistory = FixtureFactory.createClothRecommendationHistory(
            user,
            "Seoul",
            listOf(coldWeather),
            listOf(winterCoat, scarf),
            emptyList()
        )

        whenever(clothRecommendationHistoryRepository.findByDateAndLocation(hotWeather.date, "Seoul"))
            .thenReturn(listOf(hotHistory))
        whenever(clothRecommendationHistoryRepository.findByDateAndLocation(mildWeather.date, "Seoul"))
            .thenReturn(listOf(mildHistory))
        whenever(clothRecommendationHistoryRepository.findByDateAndLocation(coldWeather.date, "Seoul"))
            .thenReturn(listOf(coldHistory))

        // 4. 서비스 호출
        val result = clothService.getOutfitRecommendations(weatherPlan, "Seoul")

        // 5. 검증
        assertThat(result.recommendedOutfits[Category.TOP]).containsExactlyInAnyOrder(summerTee, formalShirt)
        assertThat(result.recommendedOutfits[Category.BOTTOM]).containsExactlyInAnyOrder(shorts, jeans)
        assertThat(result.recommendedOutfits[Category.EXTRA]).containsExactlyInAnyOrder(springJacket, winterCoat, scarf)
        assertThat(result.recommendedOutfits[Category.SHOES]).containsExactlyInAnyOrder(runningShoes)
    }
}
