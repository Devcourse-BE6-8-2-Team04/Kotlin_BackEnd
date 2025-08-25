package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.cloth.cloth.entity.ExtraCloth;
import com.team04.back.domain.weather.weather.enums.Weather;
import lombok.Getter;

@Getter
public class ExtraClothDto {

    private int id;
    private String clothName;
    private String imageUrl;
    private Weather weather;


    public ExtraClothDto(ExtraCloth extraCloth) {
        this.id = extraCloth.getId();
        this.clothName = extraCloth.getClothName();
        this.imageUrl = extraCloth.getImageUrl();
        this.weather = extraCloth.getWeather();
    }
}
