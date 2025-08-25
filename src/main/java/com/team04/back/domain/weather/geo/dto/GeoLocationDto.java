package com.team04.back.domain.weather.geo.dto;

import com.team04.back.infra.weather.dto.GeoDirectResponse;
import jakarta.validation.constraints.NotNull;

public record GeoLocationDto(
        @NotNull String name,
        @NotNull String country,
        @NotNull double lat,
        @NotNull double lon,
        String localName
) {
    public GeoLocationDto(GeoDirectResponse geoDirectResponse) {
        this(
                geoDirectResponse.getName(),
                geoDirectResponse.getCountry(),
                geoDirectResponse.getLat(),
                geoDirectResponse.getLon(),
                geoDirectResponse.getLocalNames() != null
                        ? geoDirectResponse.getLocalNames().getKorean()
                        : null
        );
    }
}
