package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.weather.weather.dto.WeatherInfoDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class WeatherClothResponseDto {
    private WeatherInfoDto weatherInfo;
    private List<CategoryClothDto>  clothList;
    private Set<ExtraClothDto> extraCloth;

    public WeatherClothResponseDto(WeatherInfoDto weatherInfo, List<CategoryClothDto> clothList, Set<ExtraClothDto> extraCloth) {
        this.weatherInfo = weatherInfo;
        this.clothList = clothList;
        this.extraCloth = extraCloth;
    }
}
