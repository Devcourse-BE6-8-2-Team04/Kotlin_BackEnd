package com.team04.back.domain.weather.weather.controller

import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.service.WeatherService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/weathers")
@Tag(name = "Weather", description = "날씨 정보 API")
@Validated
class WeatherController(
    private val weatherService: WeatherService,
    private val geoService: GeoService
) {

    @GetMapping
    @Operation(summary = "주간 날씨 조회", description = "위도와 경도를 이용하여 주간 날씨 정보를 조회합니다.")
    fun getWeeklyWeather(
        @RequestParam(required = false, defaultValue = "unknown") location: String,
        @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") lat: Double,
        @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") lon: Double,
    ): List<WeatherInfoDto> {
        val weatherInfos: List<WeatherInfo> = weatherService.getWeeklyWeather(location, lat, lon)
        return weatherInfos.map { WeatherInfoDto(it) }
    }

    @GetMapping("/{date}")
    @Operation(summary = "특정 날짜 날씨 조회", description = "위도와 경도를 이용하여 특정 날짜의 날씨 정보를 조회합니다.")
    fun getWeatherByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") lat: Double,
        @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") lon: Double,
    ): WeatherInfoDto {
        val weatherInfo: WeatherInfo = weatherService.getWeatherInfo(lat, lon, date)
        return WeatherInfoDto(weatherInfo)
    }

    @GetMapping("/location")
    @Operation(summary = "위치별 날씨 조회", description = "지명과 위도와 경도를 이용하여 해당 위치의 날씨 정보를 조회합니다.")
    fun getWeatherByLocation(
        @RequestParam @NotBlank location: String,
        @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") lat: Double,
        @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") lon: Double,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) start: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end: LocalDate
    ): List<WeatherInfoDto> {
        val weatherInfos: List<WeatherInfo> = weatherService.getWeatherInfos(location, lat, lon, start, end)
        return weatherInfos.map { WeatherInfoDto(it) }
    }
}
