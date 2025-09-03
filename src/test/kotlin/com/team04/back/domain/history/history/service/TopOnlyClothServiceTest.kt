package com.team04.back.domain.cloth.cloth.service

import com.team04.back.domain.cloth.cloth.dto.OutfitRecommendationResponseDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.repository.ClothRepository
import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate

class TopOnlyClothServiceTest {

    val clothRepository = mock(ClothRepository::class.java)
    val historyRepository = mock(ClothRecommendationHistoryRepository::class.java)

    val topOnlyService = TopOnlyClothService(clothRepository, historyRepository)

    @Test
    fun `TOP 카테고리만 추천과 비추천에서 반환되는지 검증`() {
        // GIVEN
        val weather = WeatherInfo(
            weather = Weather.CLEAR_SKY,
            dailyTemperatureGap = 5.0,
            feelsLikeTemperature = 23.0,
            maxTemperature = 26.0,
            minTemperature = 21.0,
            location = "서울",
            date = LocalDate.now(),
            description = "맑음",
            pop = 0.0,
            rain = 0.0,
            snow = 0.0,
            humidity = 45,
            windSpeed = 2.5,
            windDeg = 180,
            uvi = 5.0
        )

        val topCloth1 = ClothInfo.create(
            clothName = ClothName.T_SHIRT,
            imageUrl = "image1.jpg",
            category = Category.TOP,
            style = null,
            material = null,
            minFeelsLike = null,
            maxFeelsLike = null
        )

        val pantsCloth = ClothInfo.create(
            clothName = ClothName.SLACKS,
            imageUrl = "pants.jpg",
            category = Category.BOTTOM,
            style = null,
            material = null,
            minFeelsLike = null,
            maxFeelsLike = null
        )

        val history = ClothRecommendationHistory(
            member = mock(),
            location = "서울",
            date = weather.date,
            weatherInfo = listOf(weather),
            likedClothings = listOf(topCloth1, pantsCloth),
            dislikedClothings = listOf(pantsCloth),
            feelsLike = 23.0,
            uvi = 5.0,
            rain = 0.0,
            snow = 0.0,
            humidity = 45,
            windSpeed = 2.5,
            tempMin = 21.0,
            tempMax = 26.0,
            dailyTemperatureGap = 5.0,
            reviewedAt = weather.date.atStartOfDay()
        )

        `when`(historyRepository.findByDateAndLocation(weather.date, "서울")).thenReturn(listOf(history))

        // WHEN
        val result: OutfitRecommendationResponseDto = topOnlyService.getOutfitRecommendations(
            weatherPlan = listOf(weather),
            location = "서울"
        )

        // THEN
        // 추천: T_SHIRT만 포함 (TOP)
        assertEquals(2, result.recommendedOutfits.size)
        assertEquals(true, result.recommendedOutfits.containsKey(Category.TOP))
        assertEquals(listOf(topCloth1), result.recommendedOutfits[Category.TOP])

        // 비추천: BOTTOM은 제외되어 비어 있어야 함
        assertEquals(1, result.notRecommendedOutfits.size)
    }
}
