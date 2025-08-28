package com.team04.back.domain.weather.geo.controller

import com.team04.back.domain.weather.geo.dto.GeoLocationDto
import com.team04.back.domain.weather.geo.service.GeoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/api/v1/geos")
@Tag(name = "Geo", description = "지역 정보 API")
class GeoController(
    private val geoService: GeoService
) {

    @GetMapping
    @Operation(summary = "도시 이름으로 지역 정보 조회", description = "도시 이름에 해당하는 지역 정보를 반환합니다.")
    fun getGeoLocations(
        @RequestParam @NotBlank location: String
    ): List<GeoLocationDto> =
        geoService.getGeoLocations(location)
}
