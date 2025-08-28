package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.cloth.cloth.entity.ExtraCloth;
import com.team04.back.domain.weather.weather.enums.Weather;

public record ExtraClothDto(
    int id,
    String clothName,
    String imageUrl,
    Weather weather
) {
    public ExtraClothDto(ExtraCloth extraCloth) {
        this(
            extraCloth.getId(),
            extraCloth.getClothName(),
            extraCloth.getImageUrl(),
            extraCloth.getWeather()
        );
    }
}