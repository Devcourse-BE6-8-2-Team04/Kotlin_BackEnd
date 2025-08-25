package com.team04.back.infra.weather;

import com.team04.back.infra.weather.dto.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WeatherApiClientImpl implements WeatherApiClient {

    private final WebClient webClient;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Override
    public Mono<OneCallApiResponse> fetchOneCallWeatherData(@NotNull double lat, @NotNull double lon, List<String> exclude, String units, String lang) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/3.0/onecall")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("exclude", String.join(",", exclude))
                        .queryParam("units", units)
                        .queryParam("lang", lang)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OneCallApiResponse.class);
    }

    @Override
    public Mono<TimeMachineApiResponse> fetchTimeMachineWeatherData(@NotNull double lat, @NotNull double lon, @NotNull long dt, String units, String lang) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/3.0/onecall/timemachine")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("dt", dt)
                        .queryParam("units", units)
                        .queryParam("lang", lang)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TimeMachineApiResponse.class);
    }

    @Override
    public Mono<DaySummaryApiResponse> fetchDaySummaryWeatherData(@NotNull double lat, @NotNull double lon, @NotNull String date, String tz, String units) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/3.0/onecall/day_summary")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("date", date)
                        .queryParam("tz", tz)
                        .queryParam("units", units)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(DaySummaryApiResponse.class);
    }

    @Override
    public Mono<WeatherOverviewApiResponse> fetchWeatherOverview(@NotNull double lat, @NotNull double lon, String date, String units) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/3.0/onecall/overview")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("date", date)
                        .queryParam("units", units)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(WeatherOverviewApiResponse.class);
    }

    @Override
    public Mono<List<GeoDirectResponse>> fetchCoordinatesByCity(String city, String countryCode, Integer limit) {
        String query = countryCode != null ? city + "," + countryCode : city;
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/direct")
                        .queryParam("q", query)
                        .queryParam("limit", limit != null ? limit : 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(GeoDirectResponse.class)
                .collectList();
    }

    @Override
    public Mono<List<GeoReverseResponse>> fetchCityByCoordinates(double lat, double lon, Integer limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/reverse")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("limit", limit != null ? limit : 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToFlux(GeoReverseResponse.class)
                .collectList();
    }
}
