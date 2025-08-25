package com.team04.back.domain.weather.geo.service;

import com.team04.back.domain.weather.geo.dto.GeoLocationDto;
import com.team04.back.infra.weather.WeatherApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeoService {
    private final WeatherApiClient weatherApiClient;

    /**
     * 주어진 도시 이름에 대한 지역 정보를 가져옵니다.
     * @param location 도시 이름
     * @return 도시에 대한 지역 정보 리스트
     */
    public List<GeoLocationDto> getGeoLocations(String location) {
        return weatherApiClient.fetchCoordinatesByCity(location, null, 5)
                .blockOptional()
                .map(response -> response.stream()
                        .map(GeoLocationDto::new)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }


    // 좌표를 이용하여 지역 이름 조회
    public String getLocationFromCoordinates(double lat, double lon) {
        return weatherApiClient.fetchCityByCoordinates(lat, lon, 1)
                .blockOptional()
                .flatMap(list -> list.stream().findFirst())
                .map(geo -> geo.getLocalNames().getKorean() != null ? geo.getLocalNames().getKorean() : geo.getName())
                .orElse("알 수 없음");
    }

    // 지역 이름을 이용하여 좌표 조회
    public List<Double> getCoordinatesFromLocation(String cityName, String countryCode) {
        return weatherApiClient.fetchCoordinatesByCity(cityName, countryCode, 1)
                .blockOptional()
                .flatMap(list -> list.stream().findFirst())
                .map(geo -> List.of(geo.getLat(), geo.getLon()))
                .orElse(List.of(0.0, 0.0)); // 기본값으로 0.0, 0.0 반환
    }
}
