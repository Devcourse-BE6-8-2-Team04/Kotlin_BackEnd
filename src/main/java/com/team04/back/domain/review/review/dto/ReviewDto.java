package com.team04.back.domain.review.review.dto;

import com.team04.back.domain.review.review.entity.Review;
import com.team04.back.domain.weather.weather.dto.WeatherInfoDto;
import org.springframework.lang.NonNull;

public record ReviewDto(
        @NonNull int id,
        @NonNull String email,
        String imageUrl,
        @NonNull String title,
        @NonNull String sentence,
        @NonNull String tagString,
        @NonNull WeatherInfoDto weatherInfoDto
){
    public ReviewDto(Review comment){
        this(
                comment.getId(),
                comment.getEmail(),
                comment.getImageUrl(),
                comment.getTitle(),
                comment.getSentence(),
                comment.getTagString(),
                new WeatherInfoDto(comment.getWeatherInfo())
        );
    }
}
