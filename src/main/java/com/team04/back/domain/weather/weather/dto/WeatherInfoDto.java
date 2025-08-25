package com.team04.back.domain.weather.weather.dto;

import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;


public record WeatherInfoDto(
        @NotNull int id,
        @NotNull String weather,
        Integer weatherCode,
        String weatherDescription,
        @NotNull Double dailyTemperatureGap,
        @NotNull Double feelsLikeTemperature,
        @NotNull Double maxTemperature,
        @NotNull Double minTemperature,
        Double pop,
        Double rain,
        Double snow,
        Integer humidity,
        Double windSpeed,
        Integer windDeg,
        Double uvi,
        @NotNull String location,
        @NotNull LocalDate date
) {
    public WeatherInfoDto(WeatherInfo weatherInfo) {
        this (
                weatherInfo.getId(),
                weatherInfo.getWeather().name(),
                weatherInfo.getWeather().getCode(),
                weatherInfo.getDescription(),
                weatherInfo.getDailyTemperatureGap(),
                weatherInfo.getFeelsLikeTemperature(),
                weatherInfo.getMaxTemperature(),
                weatherInfo.getMinTemperature(),
                weatherInfo.getPop(),
                weatherInfo.getRain(),
                weatherInfo.getSnow(),
                weatherInfo.getHumidity(),
                weatherInfo.getWindSpeed(),
                weatherInfo.getWindDeg(),
                weatherInfo.getUvi(),
                weatherInfo.getLocation(),
                weatherInfo.getDate()
        );
    }
}