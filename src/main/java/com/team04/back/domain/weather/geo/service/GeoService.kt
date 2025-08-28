package com.team04.back.domain.weather.geo.service

import com.team04.back.domain.weather.geo.dto.GeoLocationDto
import com.team04.back.infra.weather.WeatherApiClient
import org.springframework.stereotype.Service

@Service
class GeoService(
    private val weatherApiClient: WeatherApiClient
) {

    /**
     * 주어진 도시 이름에 대한 지역 정보를 가져옵니다.
     * @param location 도시 이름
     * @return 도시에 대한 지역 정보 리스트
     */
    fun getGeoLocations(location: String): List<GeoLocationDto> =
        weatherApiClient.fetchCoordinatesByCity(location, null, 5)
            .blockOptional()
            .map { response -> response.map { GeoLocationDto(it) } }
            .orElse(emptyList())

    /**
     * 좌표를 이용하여 지역 이름 조회
     * @param lat 위도
     * @param lon 경도
     * @return 지역 이름 (한국어 이름이 있으면 한국어 이름, 없으면 영어 이름, 둘 다 없으면 "알 수 없음" 반환)
     */
    fun getLocationFromCoordinates(lat: Double, lon: Double): String =
        weatherApiClient.fetchCityByCoordinates(lat, lon, 1)
            .block()
            ?.firstOrNull()
            ?.let { it.localNames?.korean ?: it.name }
            ?: "알 수 없음"

    /**
     * 지역 이름을 이용하여 좌표 조회
     * @param cityName 도시 이름
     * @param countryCode 국가 코드 (선택 사항)
     * @return 좌표 리스트 [위도, 경도] (찾지 못할 경우 기본값 [0.0, 0.0] 반환)
     */
    fun getCoordinatesFromLocation(cityName: String, countryCode: String?): List<Double> =
        weatherApiClient.fetchCoordinatesByCity(cityName, countryCode, 1)
            .blockOptional()
            .flatMap { list -> list.firstOrNull()?.let { java.util.Optional.of(it) } }
            .map { geo -> listOf(geo.lat, geo.lon) }
            .orElse(listOf(0.0, 0.0)) // 기본값으로 0.0, 0.0 반환
}
