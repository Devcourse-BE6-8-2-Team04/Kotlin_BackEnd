package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.weather.weather.dto.WeatherInfoDto;

import java.util.List;
import java.util.Set;

public record WeatherClothResponseDto(
    WeatherInfoDto weatherInfo,
    List<CategoryClothDto> clothList,
    Set<ExtraClothDto> extraCloth
) {
}