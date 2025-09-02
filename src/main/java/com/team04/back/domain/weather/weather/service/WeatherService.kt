package com.team04.back.domain.weather.weather.service

import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.repository.WeatherRepository
import com.team04.back.infra.weather.WeatherApiClient
import com.team04.back.infra.weather.dto.DailyData
import com.team04.back.infra.weather.dto.OneCallApiResponse
import com.team04.back.infra.weather.dto.TimeMachineApiResponse
import com.team04.back.infra.weather.dto.TimeMachineData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class WeatherService(
    private val weatherRepository: WeatherRepository,
    private val weatherApiClient: WeatherApiClient,
    private val geoService: GeoService
) {

    fun getDurationWeather(location: String, start: LocalDate, end: LocalDate): List<WeatherInfo> =
        weatherRepository.findByLocationAndDateBetween(location, start, end)

    fun save(weatherInfo: WeatherInfo): WeatherInfo =
        weatherRepository.save(weatherInfo)

    /**
     * 위치와 좌표를 이용하여 주간 날씨 정보를 조회합니다.
     * @param location 지역 이름 (알 수 없는 경우 "unknown"으로 전달)
     * @param lat 위도
     * @param lon 경도
     * @return 해당 위치와 좌표에 대한 주간 날씨 정보 리스트
     */
    @Transactional
    fun getWeeklyWeather(location: String, lat: Double, lon: Double): List<WeatherInfo> {
        val date = LocalDate.now()
        if (location == "unknown") {
            return getWeatherInfos(lat, lon, date, date.plusDays(6))
        } else {
            // 지역 이름 정규화
            val normalized = geoService.normalizeCityName(location, lat, lon)
            return getWeatherInfos(lat, lon, date, date.plusDays(6), normalized)
        }
    }

    /**
     * 좌표, 날짜, (선택적) 지역 이름을 이용하여 날씨 정보 단건을 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param date 조회할 날짜
     * @param location (선택적) 지역 이름
     * @return 해당 좌표와 날짜에 대한 날씨 정보
     */
    @Transactional
    fun getWeatherInfo(
        lat: Double,
        lon: Double,
        date: LocalDate,
        location: String? = null
    ): WeatherInfo {
        // 지역 이름이 제공되지 않은 경우, 좌표로부터 지역 이름 조회
        val resolvedLocation = location ?: geoService.getLocationFromCoordinates(lat, lon)

        // 기존에 저장된 날씨 정보 조회
        val weatherInfo = weatherRepository.findByLocationAndDate(resolvedLocation, date)

        // 조회 결과가 있고 유효한 경우
        if (weatherInfo != null && weatherInfo.isValid()) {
            return weatherInfo
        }

        // 조회 결과가 없거나 유효하지 않은 경우
        val info = weatherInfo ?: WeatherInfo()
        return updateWeatherInfo(info, resolvedLocation, lat, lon, date)
    }

    /**
     * 좌표, 날짜 범위, (선택적) 지역 이름을 이용하여 날씨 정보 리스트를 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param location (선택적) 지역 이름
     * @return 해당 좌표와 날짜 범위에 대한 날씨 정보 리스트
     */
    @Transactional
    fun getWeatherInfos(
        lat: Double,
        lon: Double,
        startDate: LocalDate,
        endDate: LocalDate,
        location: String? = null
    ): List<WeatherInfo> {
        // 시작 날짜와 종료 날짜 유효성 검사
        require(!startDate.isAfter(endDate)) {
            "시작 날짜($startDate)는 종료 날짜($endDate)보다 이후일 수 없습니다."
        }

        // 지역 이름이 제공되지 않은 경우, 좌표로부터 지역 이름 조회
        val resolvedLocation = location ?: geoService.getLocationFromCoordinates(lat, lon)

        // 범위 내의 모든 날짜에 대해 날씨 정보 조회
        return (0..endDate.toEpochDay() - startDate.toEpochDay()).map {
            val date = startDate.plusDays(it)
            getWeatherInfo(lat, lon, date, resolvedLocation)
        }
    }

    // ==================== Private Methods ====================

    // 요청된 날짜에 따라 날씨 정보 업데이트 함수 호출
    private fun updateWeatherInfo(info: WeatherInfo, location: String, lat: Double, lon: Double, date: LocalDate): WeatherInfo {
        val today = LocalDate.now()

        return when {
            // 요청된 날짜가 오늘 이전인 경우
            date.isBefore(today) -> updateFromTimeMachineApi(info, location, lat, lon, date)
            // 요청된 날짜가 오늘부터 7일 이내인 경우
            !date.isAfter(today.plusDays(7)) -> updateFromForecastApi(info, location, lat, lon, date)
            // 요청된 날짜가 오늘 이후 7일 이상인 경우
            else -> throw IllegalArgumentException("해당 날짜($date)에 대한 예보 데이터가 존재하지 않습니다.")
        }
    }

    // 예보 API를 통해 날씨 정보를 업데이트
    private fun updateFromForecastApi(info: WeatherInfo, location: String, lat: Double, lon: Double, date: LocalDate): WeatherInfo {
        // OpenWeatherMap One Call API를 통해 예보 데이터 조회
        val response: OneCallApiResponse? = weatherApiClient.fetchOneCallWeatherData(
            lat, lon,
            listOf("minutely", "hourly", "current", "alerts"),
            "metric",
            "kr"
        ).block()

        // 예보 데이터에서 요청된 날짜에 해당하는 DailyData 찾기
        val matchedDaily: DailyData? = response?.daily
            ?.firstOrNull { LocalDateTime.ofEpochSecond(it.dt, 0, ZoneOffset.UTC).toLocalDate().isEqual(date) }

        // 해당 날짜 데이터가 없는 경우 예외 처리
        requireNotNull(matchedDaily) { "해당 날짜($date)에 대한 예보 데이터가 존재하지 않습니다." }

        // 날씨 정보 갱신 및 저장
        info.applyDailyWeather(matchedDaily, location, date)
        return weatherRepository.save(info)
    }

    // Time Machine API를 통해 날씨 정보를 업데이트
    private fun updateFromTimeMachineApi(info: WeatherInfo, location: String, lat: Double, lon: Double, date: LocalDate): WeatherInfo {
        // LocalDate를 Unix 타임스탬프로 변환, API요구사항
        val dt = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond()

        val response: TimeMachineApiResponse? = weatherApiClient.fetchTimeMachineWeatherData(
            lat, lon, dt, "metric", "kr"
        ).block()

        if (response == null || response.data.isNullOrEmpty()) {
            throw IllegalArgumentException("해당 날짜($date)에 대한 과거 날씨 데이터가 존재하지 않습니다.")
        }

        val hourlyData: List<TimeMachineData> = response.data
        val minTemp = hourlyData.minOfOrNull { it.temp } ?: 0.0
        val maxTemp = hourlyData.maxOfOrNull { it.temp } ?: 0.0
        val data = hourlyData.first()

        info.applyHistoricalWeather(data, location, date, minTemp, maxTemp)
        return weatherRepository.save(info)
    }
}
