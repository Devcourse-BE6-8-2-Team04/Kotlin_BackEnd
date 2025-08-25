package com.team04.back.domain.weather.geo.controller;

import com.team04.back.domain.weather.geo.dto.GeoLocationDto;
import com.team04.back.domain.weather.geo.service.GeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/geos")
@Tag(name = "Geo", description = "지역 정보 API")
public class GeoController {
    private final GeoService geoService;

    @GetMapping
    @Operation(summary = "도시 이름으로 지역 정보 조회", description = "도시 이름에 해당하는 지역 정보를 반환합니다.")
    public List<GeoLocationDto> getGeoLocations(
            @RequestParam @NotBlank(message = "도시 이름은 필수입니다." ) String location
    ) {
        return geoService.getGeoLocations(location);
    }
}
