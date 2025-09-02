package com.team04.back.domain.cloth.cloth.service

import com.team04.back.common.fixture.FixtureFactory
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
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
    @DisplayName("다양한 날씨와 30개 이상의 과거 추천 기록이 있을 때, 기간별 옷차림을 정확히 추천한다.")
    fun `test outfit recommendations with extensive history and edge cases`() {
        // given
        val member = Member(
            userId = "testUser",
            password = "testPassword",
            email = "test@example.com",
            age = 30,
            gender = Gender.MALE,
            tendency = Tendency.NEUTRAL
        )
        val location = "Busan"
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(29) // 30 days

        val weatherPlan = mutableListOf<WeatherInfo>()
        val allCloths = mutableListOf(
            FixtureFactory.createClothInfo(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, 20.0, 30.0),
            FixtureFactory.createClothInfo(ClothName.SHORTS, Category.BOTTOM, Style.CASUAL_DAILY, 20.0, 30.0),
            FixtureFactory.createClothInfo(ClothName.JACKET, Category.EXTRA, Style.CASUAL_DAILY, 15.0, 25.0),
            FixtureFactory.createClothInfo(ClothName.JEANS, Category.BOTTOM, Style.CASUAL_DAILY, 10.0, 20.0),
            FixtureFactory.createClothInfo(ClothName.COAT, Category.EXTRA, Style.CASUAL_DAILY, 0.0, 10.0),
            FixtureFactory.createClothInfo(ClothName.SCARF, Category.EXTRA, Style.OUTDOOR, 0.0, 10.0),
            FixtureFactory.createClothInfo(ClothName.SHIRT, Category.TOP, Style.FORMAL_OFFICE, 10.0, 20.0),
            FixtureFactory.createClothInfo(ClothName.ATHLETIC_SHOES, Category.SHOES, Style.OUTDOOR, 18.0, 28.0),
            FixtureFactory.createClothInfo(ClothName.SWEATER, Category.TOP, Style.CASUAL_DAILY, 5.0, 15.0),
            FixtureFactory.createClothInfo(ClothName.SKIRT, Category.BOTTOM, Style.CASUAL_DAILY, 15.0, 25.0),
            FixtureFactory.createClothInfo(ClothName.LEATHER_BOOTS, Category.SHOES, Style.OUTDOOR, 0.0, 10.0)
        )

        // Generate 30 days of weather info and mock history
        for (i in 0..29) {
            val currentDate = startDate.plusDays(i.toLong())
            val temperature = (0..30).random().toDouble() // Random temperature
            val weather = when (temperature) {
                in 25.0..30.0 -> Weather.CLEAR_SKY
                in 15.0..24.9 -> Weather.OVERCAST_CLOUDS
                in 5.0..14.9 -> Weather.MODERATE_RAIN
                else -> Weather.SNOW
            }
            val dailyTemperatureGap = (5..15).random().toDouble()
            val rain = if (weather == Weather.LIGHT_RAIN) (0..10).random().toDouble() else 0.0
            val snow = if (weather == Weather.SNOW) (0..5).random().toDouble() else 0.0
            val windSpeed = (0..10).random().toDouble()
            val uvi = (0..10).random().toDouble()
            val weatherInfo = FixtureFactory.createWeatherInfo(location, currentDate, weather, temperature, dailyTemperatureGap, rain, snow, windSpeed, uvi)
            weatherPlan.add(weatherInfo)

            // Simulate history for each day
            val recommendedCloths = mutableListOf<com.team04.back.domain.cloth.cloth.entity.ClothInfo>()
            val notRecommendedCloths = mutableListOf<com.team04.back.domain.cloth.cloth.entity.ClothInfo>()

            // Add some random cloths to recommended and not recommended
            allCloths.shuffled().take(3).forEach { recommendedCloths.add(it) }
            allCloths.shuffled().take(2).forEach { notRecommendedCloths.add(it) }

            val history = FixtureFactory.createClothRecommendationHistory(
                member,
                location,
                listOf(weatherInfo),
                recommendedCloths,
                notRecommendedCloths
            )

            // Edge case: No history for specific dates (e.g., every 5th day)
            if (i % 5 == 0) {
                whenever(clothRecommendationHistoryRepository.findByDateAndLocation(currentDate, location))
                    .thenReturn(emptyList())
            } else {
                whenever(clothRecommendationHistoryRepository.findByDateAndLocation(currentDate, location))
                    .thenReturn(listOf(history))
            }
        }

        // Service Call
        val result = clothService.getOutfitRecommendations(weatherPlan, location)

        // Assertions
        assertThat(result).isNotNull
        assertThat(result.recommendedOutfits).isNotEmpty

        // Verify that common categories have recommendations
        assertThat(result.recommendedOutfits).containsKey(Category.TOP)
        assertThat(result.recommendedOutfits[Category.TOP]).isNotEmpty
        assertThat(result.recommendedOutfits).containsKey(Category.BOTTOM)
        assertThat(result.recommendedOutfits[Category.BOTTOM]).isNotEmpty
        assertThat(result.recommendedOutfits).containsKey(Category.EXTRA)
        assertThat(result.recommendedOutfits[Category.EXTRA]).isNotEmpty
        assertThat(result.recommendedOutfits).containsKey(Category.SHOES)
        assertThat(result.recommendedOutfits[Category.SHOES]).isNotEmpty
    }

    @Test
    @DisplayName("날씨 계획이 비어있을 때, 빈 옷차림 추천 결과를 반환한다.")
    fun `test outfit recommendations with empty weather plan`() {
        // given
        val emptyWeatherPlan = emptyList<WeatherInfo>()
        val location = "Seoul"

        // when
        val result = clothService.getOutfitRecommendations(emptyWeatherPlan, location)

        // then
        assertThat(result).isNotNull
        assertThat(result.recommendedOutfits).isEmpty()
    }
}
