package com.team04.back.infra.weather

import com.team04.back.infra.weather.dto.*
import jakarta.validation.constraints.NotNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class WeatherApiClientImpl(
    private val webClient: WebClient
) : WeatherApiClient {

    @Value("\${openweather.api.key}")
    private lateinit var apiKey: String

    override fun fetchOneCallWeatherData(
        @NotNull lat: Double,
        @NotNull lon: Double,
        exclude: List<String>,
        units: String,
        lang: String
    ): Mono<OneCallApiResponse> =
        webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/data/3.0/onecall")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("exclude", exclude.joinToString(","))
                    .queryParam("units", units)
                    .queryParam("lang", lang)
                    .queryParam("appid", apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono(OneCallApiResponse::class.java)

    override fun fetchTimeMachineWeatherData(
        @NotNull lat: Double,
        @NotNull lon: Double,
        @NotNull dt: Long,
        units: String,
        lang: String
    ): Mono<TimeMachineApiResponse> =
        webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/data/3.0/onecall/timemachine")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("dt", dt)
                    .queryParam("units", units)
                    .queryParam("lang", lang)
                    .queryParam("appid", apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono(TimeMachineApiResponse::class.java)

    override fun fetchDaySummaryWeatherData(
        @NotNull lat: Double,
        @NotNull lon: Double,
        @NotNull date: String,
        tz: String,
        units: String
    ): Mono<DaySummaryApiResponse> =
        webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/data/3.0/onecall/day_summary")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("date", date)
                    .queryParam("tz", tz)
                    .queryParam("units", units)
                    .queryParam("appid", apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono(DaySummaryApiResponse::class.java)

    override fun fetchWeatherOverview(
        @NotNull lat: Double,
        @NotNull lon: Double,
        date: String,
        units: String
    ): Mono<WeatherOverviewApiResponse> =
        webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/data/3.0/onecall/overview")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("date", date)
                    .queryParam("units", units)
                    .queryParam("appid", apiKey)
                    .build()
            }
            .retrieve()
            .bodyToMono(WeatherOverviewApiResponse::class.java)

    override fun fetchCoordinatesByCity(
        city: String,
        countryCode: String?,
        limit: Int?
    ): Mono<List<GeoDirectResponse>> {
        val query = if (countryCode != null) "$city,$countryCode" else city
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/geo/1.0/direct")
                    .queryParam("q", query)
                    .queryParam("limit", limit ?: 1)
                    .queryParam("appid", apiKey)
                    .build()
            }
            .retrieve()
            .bodyToFlux(GeoDirectResponse::class.java)
            .collectList()
    }

    override fun fetchCityByCoordinates(
        lat: Double,
        lon: Double,
        limit: Int?
    ): Mono<List<GeoReverseResponse>> =
        webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/geo/1.0/reverse")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("limit", limit ?: 1)
                    .queryParam("appid", apiKey)
                    .build()
            }
            .retrieve()
            .bodyToFlux(GeoReverseResponse::class.java)
            .collectList()
}
