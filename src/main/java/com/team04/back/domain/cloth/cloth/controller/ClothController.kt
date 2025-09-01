package com.team04.back.domain.cloth.cloth.controller

import com.team04.back.domain.cloth.cloth.dto.OutfitResponseDto
import com.team04.back.domain.cloth.cloth.dto.TripScheduleDto
import com.team04.back.domain.cloth.cloth.dto.WeatherClothResponseDto
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto
import com.team04.back.domain.weather.weather.service.WeatherService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/cloth")
class ClothController(
    private val clothService: ClothService,
    private val weatherService: WeatherService
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
    fun getOutfitWithPeriod(@ModelAttribute tripSchedule: TripScheduleDto): OutfitResponseDto {
        val duration = weatherService.getWeatherInfos(
            tripSchedule.lat,
            tripSchedule.lon,
            tripSchedule.start!!,
            tripSchedule.end!!
        )
        val outfits = clothService.getOutfitWithPeriod(duration)
        return OutfitResponseDto(outfits)
    }
}
