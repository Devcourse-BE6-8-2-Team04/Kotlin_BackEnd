package com.team04.back.domain.weather.geo.service

import com.team04.back.domain.weather.geo.dto.GeoLocationDto
import com.team04.back.infra.cache.GeoCache
import com.team04.back.infra.weather.WeatherApiClient
import org.springframework.stereotype.Service

@Service
class GeoService(
    private val weatherApiClient: WeatherApiClient,
    private val geoCache: GeoCache
) {

    /**
     * 주어진 도시 이름에 대한 지역 정보를 가져옵니다.
     * @param location 도시 이름
     * @return 도시에 대한 지역 정보 리스트
     */
    fun getGeoLocations(location: String): List<GeoLocationDto> {
        // 캐시에서 조회
        geoCache.getGeoLocations(location)?.let { return it }

        // 캐시에 없으면 외부 API 호출
        val result = weatherApiClient.fetchCoordinatesByCity(location, null, 5)
            .blockOptional()
            .map { response -> response.map { GeoLocationDto(it) } }
            .orElse(emptyList())

        // 결과를 캐시에 저장
        if (result.isNotEmpty()) {
            geoCache.putGeoLocations(location, result)
        }

        return result
    }

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

    /**
     * 도시 이름 정규화
     * @param rawCityName 원시 도시 이름
     * @return 정규화된 도시 이름
     */
    fun normalizeCityName(rawCityName: String, lat: Double, lon: Double): String {
        // 캐시에서 조회
        geoCache.getNormalizedCity(rawCityName)?.let { return it }

        // 캐시에 없으면 좌표로 지역 이름 조회
        val normalized = getLocationFromCoordinates(lat, lon)

        // 결과를 캐시에 저장
        if (normalized != "알 수 없음") {
            geoCache.putNormalizedCity(rawCityName, normalized)
        }

        return normalized
    }
}
