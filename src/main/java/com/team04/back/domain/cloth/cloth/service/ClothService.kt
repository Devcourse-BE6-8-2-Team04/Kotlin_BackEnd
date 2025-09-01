package com.team04.back.domain.cloth.cloth.service

import com.team04.back.domain.cloth.cloth.dto.CategoryClothDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.cloth.cloth.repository.ClothRepository
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ClothService(
    private val clothRepository: ClothRepository,
) {

    fun findClothByWeather(feelsLikeTemperature: Double?): List<CategoryClothDto> {
        return clothRepository
            .findByMinFeelsLikeLessThanEqualAndMaxFeelsLikeGreaterThanEqual(
                feelsLikeTemperature,
                feelsLikeTemperature
            )
            .map { cloth -> CategoryClothDto(cloth.clothName, cloth.imageUrl, cloth.category, cloth.style, cloth.material) }
    }

    fun getOutfitWithPeriod(weatherPlan: List<WeatherInfo>): Map<Style, MutableList<ClothInfo>> {
        val recommendedClothesMap = mutableMapOf<Style, MutableList<ClothInfo>>()

        for (weather in weatherPlan) {
            val recommendedClothes = clothRepository.findByTemperature(weather.feelsLikeTemperature)

            for (cloth in recommendedClothes) {
                recommendedClothesMap.getOrPut(cloth.style!!) { mutableListOf() }.add(cloth)
            }

        }

        recommendedClothesMap.getOrPut(Style.EXTRA) { mutableListOf() }

        return recommendedClothesMap
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
}
