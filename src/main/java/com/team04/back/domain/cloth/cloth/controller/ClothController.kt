package com.team04.back.domain.cloth.cloth.controller

import com.team04.back.domain.cloth.cloth.dto.OutfitRecommendationResponseDto
import com.team04.back.domain.cloth.cloth.dto.WeatherClothResponseDto
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto
import com.team04.back.domain.weather.weather.service.WeatherService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/cloth")
class ClothController(
    private val clothService: ClothService,
    private val weatherService: WeatherService,
) {

    @GetMapping("/details")
    @Operation(summary = "날씨 기반 옷 정보 조회", description = "위도와 경도를 이용하여 날씨 정보를 조회하고, 해당 날씨에 적합한 옷 정보를 반환합니다.")
    fun getClothDetails(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double
    ): WeatherClothResponseDto {
        val weatherInfo = weatherService.getWeatherInfo(latitude, longitude, LocalDate.now())
        val cloths = clothService.findClothByWeather(weatherInfo.feelsLikeTemperature)
        return WeatherClothResponseDto(WeatherInfoDto(weatherInfo), cloths)
    }

    @GetMapping
    @Operation(summary = "기간별 옷차림 조회", description = "시작일과 종료일을 이용하여 해당 기간 동안의 날씨에 적합한 옷차림 정보를 반환합니다.")
    fun getOutfitWithPeriod(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam location: String,
        @RequestParam startDate: LocalDate,
        @RequestParam endDate: LocalDate
    ): OutfitRecommendationResponseDto {
        // 1. 기간 동안 날씨 계획 조회
        val weatherPlan = weatherService.getWeatherInfos(
            latitude,
            longitude,
            startDate,
            endDate
        )

        // 2. ClothService에서 추천/비추천 의류 계산
        val outfitRecommendations : OutfitRecommendationResponseDto = clothService.getOutfitRecommendations(weatherPlan, location)
        return outfitRecommendations
    }
}
