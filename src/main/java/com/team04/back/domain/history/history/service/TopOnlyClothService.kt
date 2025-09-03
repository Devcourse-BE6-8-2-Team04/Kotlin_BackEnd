package com.team04.back.domain.cloth.cloth.service

import com.team04.back.domain.cloth.cloth.dto.OutfitRecommendationResponseDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.repository.ClothRepository
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class TopOnlyClothService(
    clothRepository: ClothRepository,
    clothRecommendationHistoryRepository: ClothRecommendationHistoryRepository
) : ClothService(clothRepository, clothRecommendationHistoryRepository) {

    override fun getOutfitRecommendations(
        weatherPlan: List<WeatherInfo>,
        location: String,
        limit: Int
    ): OutfitRecommendationResponseDto {
        val (recommendedMap, notRecommendedMap) = collectClothingStats(weatherPlan, location, limit)

        val finalRecommended = filterTopScores(recommendedMap)
        val finalNotRecommended = filterTopScores(notRecommendedMap)

        return OutfitRecommendationResponseDto(
            recommendedOutfits = finalRecommended,
            notRecommendedOutfits = finalNotRecommended
        )
    }

    // 공통 로직: 추천/비추천 누적
    private fun collectClothingStats(
        weatherPlan: List<WeatherInfo>,
        location: String,
        limit: Int
    ): Pair<Map<Category, MutableMap<ClothInfo, Int>>, Map<Category, MutableMap<ClothInfo, Int>>> {

        val recommendedMap = mutableMapOf<Category, MutableMap<ClothInfo, Int>>()
        val notRecommendedMap = mutableMapOf<Category, MutableMap<ClothInfo, Int>>()

        for (weather in weatherPlan) {
            var histories = clothRecommendationHistoryRepository.findByDateAndLocation(weather.date, location)

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

            histories.forEach { history ->
                history.likedClothings.forEach { cloth ->
                    val key = cloth.category
                    recommendedMap.getOrPut(key) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
                history.dislikedClothings.forEach { cloth ->
                    val key = cloth.category
                    notRecommendedMap.getOrPut(key) { mutableMapOf() }
                        .merge(cloth, 1, Int::plus)
                }
            }
        }

        return recommendedMap to notRecommendedMap
    }

    // 필터링 로직: 최고 점수만 추출
    private fun filterTopScores(
        map: Map<Category, MutableMap<ClothInfo, Int>>
    ): Map<Category, List<ClothInfo>> {
        return map.mapValues { (_, clothMap) ->
            val maxScore = clothMap.values.maxOrNull() ?: 0
            clothMap.filterValues { it == maxScore }.keys.toList()
        }
    }
}
