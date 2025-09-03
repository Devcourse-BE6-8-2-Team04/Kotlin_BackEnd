package com.team04.back.domain.cloth.cloth.controller

import com.team04.back.domain.cloth.cloth.dto.OutfitRecommendationResponseDto
import com.team04.back.domain.cloth.cloth.dto.WeatherClothResponseDto
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.cloth.cloth.service.TopOnlyClothService
import com.team04.back.domain.weather.geo.service.GeoService
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
    private val geoService: GeoService,
    private val topOnlyClothService: TopOnlyClothService,

    ) {
    @GetMapping("/details")
    @Operation(summary = "날씨 기반 옷 정보 조회", description = "위도와 경도를 이용하여 날씨 정보를 조회하고, 해당 날씨에 적합한 옷 정보를 반환합니다.")
    fun getClothDetails(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
    ): WeatherClothResponseDto  {
        // 1. 위도, 경도로 위치 조회
        val location = geoService.getLocationFromCoordinates(latitude, longitude)

        // 2. 현재 날씨 조회
        val weatherInfo = weatherService.getWeatherInfo(latitude, longitude, LocalDate.now())

        // 3. 오늘 날짜 범위로 날씨 계획을 만들어서 서비스 호출
        val weatherPlan = weatherService.getWeatherInfos(latitude, longitude, LocalDate.now(), LocalDate.now())


        // 4. 현재 날씨에 맞는 옷차림 추천 조회
        val outfitRecommendations : OutfitRecommendationResponseDto = topOnlyClothService.getOutfitRecommendations(weatherPlan, location)

        return WeatherClothResponseDto(
            weatherInfo = weatherInfo,
            recommendedOutfits = outfitRecommendations.recommendedOutfits,
            notRecommendedOutfits = outfitRecommendations.notRecommendedOutfits
        )
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
