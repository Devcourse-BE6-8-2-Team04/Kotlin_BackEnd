package com.team04.back.domain.cloth.cloth.service

import com.team04.back.domain.cloth.cloth.dto.CategoryClothDto
import com.team04.back.domain.cloth.cloth.dto.OutfitRecommendationResponseDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.repository.ClothRepository
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ClothService(
    private val clothRepository: ClothRepository,
    private val clothRecommendationHistoryRepository : ClothRecommendationHistoryRepository
) {

    fun findClothByWeather(feelsLikeTemperature: Double?): List<CategoryClothDto> {
        return clothRepository
            .findByMinFeelsLikeLessThanEqualAndMaxFeelsLikeGreaterThanEqual(
                feelsLikeTemperature,
                feelsLikeTemperature
            )
            .map { cloth -> CategoryClothDto(cloth.clothName, cloth.imageUrl, cloth.category, cloth.style, cloth.material) }
    }

    fun getOutfitRecommendations(
        weatherPlan: List<WeatherInfo>,
        location : String
    ): OutfitRecommendationResponseDto {
        val recommendedMap = mutableMapOf<Category, MutableMap<ClothInfo, Int>>() // 의류별 추천 횟수
        val notRecommendedMap = mutableMapOf<Category, MutableMap<ClothInfo, Int>>() // 의류별 비추천 횟수

        for (weather in weatherPlan) {
            // 1. 현재 날씨에 해당하는 ClothRecommendationHistory 조회
            var histories = clothRecommendationHistoryRepository.findByDateAndLocation(
                weather.date, location
            )

            // 2. 없다면 유사 날씨 기반 과거 데이터 조회
            if (histories.isEmpty()) {
                val pastWeathers = clothRecommendationHistoryRepository.findByDateBetweenAndLocation(
                    weather.date.minusYears(1),
                    weather.date.minusYears(0),
                    location
                )
                val similarWeathers = pastWeathers.filter { past ->
                    past.weatherInfo.any { pastWeather ->
                        isWeatherInfoSimilar(weather, pastWeather)
                    }
                }
                histories = similarWeathers.flatMap {
                    clothRecommendationHistoryRepository.findByDateAndLocation(it.date, location)
                }
            }

            // 3. 추천/비추천 의류 종합
            for (history in histories) {
                history.likedClothings.forEach { cloth ->
                    recommendedMap.getOrPut(cloth.category!!) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
                history.dislikedClothings.forEach { cloth ->
                    notRecommendedMap.getOrPut(cloth.category!!) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
            }
        }

        // 4. 추천/비추천 의류 횟수 기반 정리
        val finalRecommended = recommendedMap.mapValues { it.value.keys.toList() }
        val finalNotRecommended = notRecommendedMap.mapValues { it.value.keys.toList() }

        return OutfitRecommendationResponseDto(
            recommendedOutfits = finalRecommended,
            notRecommendedOutfits = finalNotRecommended
        )
    }


    private fun getWeatherGroup(weather: WeatherInfo): Weather {
        val code = weather.weather.code

        return when {
            // 폭염 - 체감기온 30 이상
            weather.feelsLikeTemperature >= 30 -> Weather.HEAT_WAVE
            // 비 또는 뇌우
            (code in 200..399) || (code in 500..599) -> Weather.MODERATE_RAIN
            // 눈
            (code in 600..699) -> Weather.SNOW
            // 안개 또는 먼지
            (code in 700..799) -> Weather.MIST
            // 그 외는 맑은 하늘
            else -> Weather.CLEAR_SKY
        }
    }

    @Transactional
    fun save(clothInfo: ClothInfo) {
        clothRepository.save(clothInfo)
    }

    fun count(): Long = clothRepository.count()

    fun isWeatherInfoSimilar(info1: WeatherInfo, info2: WeatherInfo): Boolean {
        fun closeEnough(a: Double?, b: Double?, tolerance: Double) =
            if (a != null && b != null) kotlin.math.abs(a - b) < tolerance else true

        return kotlin.math.abs(info1.feelsLikeTemperature - info2.feelsLikeTemperature) <= 3.0 &&
                closeEnough(info1.rain, info2.rain, 1.0) &&
                closeEnough(info1.snow, info2.snow, 1.0) &&
                closeEnough(info1.windSpeed, info2.windSpeed, 3.0) &&
                closeEnough(info1.uvi, info2.uvi, 2.0) &&
                kotlin.math.abs(info1.dailyTemperatureGap - info2.dailyTemperatureGap) < 5.0
    }
}
