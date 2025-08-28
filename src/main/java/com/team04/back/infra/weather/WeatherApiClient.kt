package com.team04.back.infra.weather

import com.team04.back.infra.weather.dto.*
import jakarta.validation.constraints.NotNull
import reactor.core.publisher.Mono

/**
 * Weather API Client 인터페이스
 */
interface WeatherApiClient {

    /**
     * Current and forecasts weather data.
     * 현재 및 예보 날씨 데이터를 가져옵니다.
     * @param lat 위도, 십진수 (-90; 90). (필수)
     * @param lon 경도, 십진수 (-180; 180). (필수)
     * @param exclude API 응답에서 제외할 날씨 데이터 부분 (예: ["current", "minutely"]). (선택 사항)
     * @param units 측정 단위. "standard", "metric", "imperial" 사용 가능. (선택 사항)
     * @param lang 출력 언어. (선택 사항)
     * @return 구조화된 DTO로 날씨 데이터를 포함하는 Mono.
     */
    fun fetchOneCallWeatherData(
        @NotNull lat: Double,
        @NotNull lon: Double,
        exclude: List<String>,
        units: String,
        lang: String
    ): Mono<OneCallApiResponse>

    /**
     * Weather data for timestamp.
     * 특정 타임스탬프에 대한 날씨 데이터를 가져옵니다.
     * @param lat 위도, 십진수 (-90; 90). (필수)
     * @param lon 경도, 십진수 (-180; 180). (필수)
     * @param dt 타임스탬프 (Unix 시간, UTC 시간대). (필수)
     * @param units 측정 단위. "standard", "metric", "imperial" 사용 가능. (선택 사항)
     * @param lang 출력 언어. (선택 사항)
     * @return 구조화된 DTO로 날씨 데이터를 포함하는 Mono.
     */
    fun fetchTimeMachineWeatherData(
        @NotNull lat: Double,
        @NotNull lon: Double,
        @NotNull dt: Long,
        units: String,
        lang: String
    ): Mono<TimeMachineApiResponse>

    /**
     * Daily Aggregation.
     * 특정 날짜에 대한 집계된 날씨 데이터를 가져옵니다.
     * @param lat 위도, 십진수 (-90; 90). (필수)
     * @param lon 경도, 십진수 (-180; 180). (필수)
     * @param date YYYY-MM-DD 형식의 날짜. (필수)
     * @param tz 시간대 (±XX:XX 형식). (선택 사항)
     * @param units 측정 단위. "standard", "metric", "imperial" 사용 가능. (선택 사항)
     * @return 구조화된 DTO로 일별 요약 날씨 데이터를 포함하는 Mono.
     */
    fun fetchDaySummaryWeatherData(
        @NotNull lat: Double,
        @NotNull lon: Double,
        @NotNull date: String,
        tz: String,
        units: String
    ): Mono<DaySummaryApiResponse>

    /**
     * Weather overview
     * 사람이 읽을 수 있는 형태의 날씨 요약을 가져옵니다.
     * @param lat 위도, 십진수 (-90; 90). (필수)
     * @param lon 경도, 십진수 (-180; 180). (필수)
     * @param date YYYY-MM-DD 형식의 날짜. (선택 사항)
     * @param units 측정 단위. "standard", "metric", "imperial" 사용 가능. (선택 사항)
     * @return 구조화된 DTO로 날씨 개요를 포함하는 Mono.
     */
    fun fetchWeatherOverview(
        @NotNull lat: Double,
        @NotNull lon: Double,
        date: String,
        units: String
    ): Mono<WeatherOverviewApiResponse>

    /**
     * Fetch coordinates by city name.
     * 도시 이름으로 좌표를 가져옵니다.
     * @param cityName 도시 이름. (필수)
     * @param countryCode 국가 코드 (예: "KR"). (선택 사항)
     * @param limit 최대 결과 수. (선택 사항)
     * @return 도시 이름에 해당하는 좌표 목록을 포함하는 Mono.
     */
    fun fetchCoordinatesByCity(
        @NotNull cityName: String,
        countryCode: String?,
        limit: Int?
    ): Mono<List<GeoDirectResponse>>

    /**
     * Fetch city by coordinates.
     * 좌표로 도시 정보를 가져옵니다.
     * @param lat 위도, 십진수 (-90; 90). (필수)
     * @param lon 경도, 십진수 (-180; 180). (필수)
     * @param limit 최대 결과 수. (선택 사항)
     * @return 좌표에 해당하는 도시 정보 목록을 포함하는 Mono.
     */
    fun fetchCityByCoordinates(
        @NotNull lat: Double,
        @NotNull lon: Double,
        limit: Int?
    ): Mono<List<GeoReverseResponse>>
}
