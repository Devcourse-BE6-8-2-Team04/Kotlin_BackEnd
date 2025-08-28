package com.team04.back.domain.cloth.cloth.controller

import com.team04.back.domain.cloth.cloth.dto.ExtraClothDto
import com.team04.back.domain.cloth.cloth.dto.OutfitResponseDto
import com.team04.back.domain.cloth.cloth.dto.WeatherClothResponseDto
import com.team04.back.domain.cloth.cloth.entity.Clothing
import com.team04.back.domain.cloth.cloth.entity.ExtraCloth
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto
import com.team04.back.domain.weather.weather.service.WeatherService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/v1/cloth")
@RequiredArgsConstructor
class ClothController {
    private val clothService: ClothService? = null
    private val weatherService: WeatherService? = null

    @GetMapping("/details")
    @Operation(summary = "날씨 기반 옷 정보 조회", description = "위도와 경도를 이용하여 날씨 정보를 조회하고, 해당 날씨에 적합한 옷 정보를 반환합니다.")
    fun getClothDetails(
        @Parameter(description = "위도", example = "37.5") @RequestParam(name = "latitude") latitude: Double,

        @Parameter(description = "경도", example = "127.0") @RequestParam(name = "longitude") longitude: Double
    ): WeatherClothResponseDto {
        //좌표 기반으로 날씨 정보 가져오기
        val weatherInfo = weatherService!!.getWeatherInfo(latitude, longitude, LocalDate.now())

        // 날씨 정보에 따라 옷 정보 가져오기
        val cloths = clothService!!.findClothByWeather(weatherInfo.feelsLikeTemperature)

        // 날씨 정보에 따라 추가 옷 정보 가져오기
        val extraCloth = clothService.getExtraClothes(weatherInfo)

        // 날씨 정보와 옷 정보를 포함한 응답 DTO 리턴
        val weatherInfoDto = WeatherInfoDto(weatherInfo)
        val extraClothDto = extraCloth.stream()
            .map { extraCloth: ExtraCloth? -> ExtraClothDto(extraCloth!!) }
            .collect(Collectors.toSet())
        return WeatherClothResponseDto(weatherInfoDto, cloths, extraClothDto)
    }

    @JvmRecord
    data class TripSchedule(
        val start: @FutureOrPresent LocalDate?, val end: @FutureOrPresent LocalDate?, val lat: @Max(
            90
        ) @Min(-90) Double, val lon: @Max(180) @Min(
            -180
        ) Double
    ) {
        init {
            if (start != null && end != null) {
                require(start.isBefore(end)) { "시작 날짜는 종료 날짜보다 이전이어야 합니다." }
                require(ChronoUnit.DAYS.between(start, end) <= 30) { "최대 30일까지 조회할 수 있습니다." }
            }
        }
    }

    @GetMapping
    fun getOutfitWithPeriod(tripSchedule: TripSchedule): OutfitResponseDto {
        val duration = weatherService!!.getWeatherInfos(
            tripSchedule.lat,
            tripSchedule.lon,
            tripSchedule.start!!,
            tripSchedule.end!!
        )
        val outfits: Map<Category, List<Clothing?>?> = clothService!!.getOutfitWithPeriod(duration)
        return OutfitResponseDto(outfits)
    }
}
