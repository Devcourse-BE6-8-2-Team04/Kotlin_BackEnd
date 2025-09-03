package com.team04.back.domain.cloth.cloth.service

import com.team04.back.domain.cloth.cloth.dto.OutfitRecommendationResponseDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.cloth.cloth.repository.ClothRepository
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
open class ClothService(
    private val clothRepository: ClothRepository,
    protected val clothRecommendationHistoryRepository: ClothRecommendationHistoryRepository
) {

    fun getOutfitRecommendations(
        weatherPlan: List<WeatherInfo>,
        location: String
    ): OutfitRecommendationResponseDto {
        //상태값 뎊스 너무 깊다, DTO로 빼던가
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
                    recommendedMap.getOrPut(cloth.category) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
                history.dislikedClothings.forEach { cloth ->
                    notRecommendedMap.getOrPut(cloth.category) { mutableMapOf() }
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

    fun getOutfitRecommendations(
        weatherPlan: List<WeatherInfo>,
        location: String,
        limit: Int = 5
    ): OutfitRecommendationResponseDto {

        val recommendedMap = mutableMapOf<Category, MutableMap<ClothInfo, Int>>()
        val notRecommendedMap = mutableMapOf<Category, MutableMap<ClothInfo, Int>>()

        for (weather in weatherPlan) {

            // 1. 현재 날씨 기록 조회
            var histories = clothRecommendationHistoryRepository.findByDateAndLocation(weather.date, location)

            // 2. 없으면 유사 날씨 기반 과거 기록 조회
            if (histories.isEmpty()) {
                val pastWeathers = clothRecommendationHistoryRepository.findByDateBetweenAndLocation(
                    weather.date.minusYears(3),
                    weather.date.minusYears(1),
                    location
                )

                val similarWeathers = pastWeathers
                    .flatMap { it.weatherInfo }
                    .filter { pastWeather -> isWeatherInfoSimilar(weather, pastWeather) }
                    .take(limit)

                histories = similarWeathers.flatMap {
                    clothRecommendationHistoryRepository.findByDateAndLocation(it.date, location)
                }
            }

            // 3. 추천/비추천 의류 종합
            histories.forEach { history ->
                history.likedClothings.forEach { cloth ->
                    val key = cloth.category ?: Category.EXTRA
                    recommendedMap.getOrPut(key) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
                history.dislikedClothings.forEach { cloth ->
                    val key = cloth.category ?: Category.EXTRA
                    notRecommendedMap.getOrPut(key) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
            }
        }

        // 4. 추천/비추천 Map 최종 정리
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
    fun save(clothInfo: ClothInfo): ClothInfo {
        return clothRepository.save(clothInfo)
    }

    fun count(): Long = clothRepository.count()

    fun isWeatherInfoSimilar(weather: WeatherInfo, otherWeather: WeatherInfo): Boolean {
        fun closeEnough(a: Double?, b: Double?, tolerance: Double) =
            if (a != null && b != null) kotlin.math.abs(a - b) < tolerance else true

        return kotlin.math.abs(weather.feelsLikeTemperature - otherWeather.feelsLikeTemperature) <= 3.0 &&
                closeEnough(weather.rain, otherWeather.rain, 1.0) &&
                closeEnough(weather.snow, otherWeather.snow, 1.0) &&
                closeEnough(weather.windSpeed, otherWeather.windSpeed, 3.0) &&
                closeEnough(weather.uvi, otherWeather.uvi, 2.0) &&
                kotlin.math.abs(weather.dailyTemperatureGap - otherWeather.dailyTemperatureGap) < 5.0
    }

    fun findByIdList(clothInfoIdList: List<Int>): List<ClothInfo> {
        return clothRepository.findAllById(clothInfoIdList)
    }

    fun findByClothNameAndStyle(clothName: ClothName, style: Style?) : ClothInfo? {
        return clothRepository.findFirstByClothNameAndStyle(clothName, style)
    }

    fun findByClothName(clothName: ClothName) : List<ClothInfo> {
        return clothRepository.findByClothName(clothName)
    }
}
