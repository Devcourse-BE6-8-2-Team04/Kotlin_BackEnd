package com.team04.back.domain.review.review.dto;

import java.time.LocalDate;

public record ReviewSearchDto(
    String location,
    LocalDate date,
    Double feelsLikeTemperature,
    Integer month,
    String email
) {
    public ReviewSearchDto(
            String location,
            LocalDate date,
            Double feelsLikeTemperature,
            Integer month,
            String email
    ) {
        this.location = location;
        this.date = date;
        this.feelsLikeTemperature = feelsLikeTemperature;
        this.month = month;
        this.email = email;
    }

    public boolean hasLocation() {
        return location != null && !location.isBlank();
    }

    public boolean hasDate() {
        return date != null;
    }

    public boolean hasFeelsLikeTemperature() {
        return feelsLikeTemperature != null;
    }

    public boolean hasMonth() {
        return month != null;
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
}