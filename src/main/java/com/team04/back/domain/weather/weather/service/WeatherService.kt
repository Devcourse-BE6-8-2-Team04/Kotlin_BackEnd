package com.team04.back.domain.weather.weather.service

import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
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
            return getWeatherInfos(normalized, lat, lon, date, date.plusDays(6))
        }
    }

    /**
     * 좌표와 날짜를 이용하여 날씨 정보 단건을 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param date 조회할 날짜
     * @return 해당 좌표와 날짜에 대한 날씨 정보
     */
    @Transactional
    fun getWeatherInfo(lat: Double, lon: Double, date: LocalDate): WeatherInfo {
        val location = geoService.getLocationFromCoordinates(lat, lon)
        return getWeatherInfo(location, lat, lon, date)
    }

    /**
     * 지역 이름과 날짜를 이용하여 날씨 정보 단건을 조회합니다.
     * @param location 지역 이름
     * @param lat 위도
     * @param lon 경도
     * @param date 조회할 날짜
     * @return 해당 지역과 날짜에 대한 날씨 정보
     */
    @Transactional
    fun getWeatherInfo(location: String, lat: Double, lon: Double, date: LocalDate): WeatherInfo {
        val weatherInfo = weatherRepository.findByLocationAndDate(location, date)

        // 조회 결과가 있고 유효한 경우
        if (weatherInfo != null && isValid(weatherInfo)) {
            return weatherInfo
        }
        // 조회 결과가 없거나 유효하지 않은 경우
        val info = weatherInfo ?: WeatherInfo()
        return updateWeatherInfo(info, location, lat, lon, date)
    }

    /**
     * 좌표와 날짜 범위를 이용하여 날씨 정보 리스트를 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 좌표와 날짜 범위에 대한 날씨 정보 리스트
     */
    @Transactional
    fun getWeatherInfos(lat: Double, lon: Double, startDate: LocalDate, endDate: LocalDate): List<WeatherInfo> {
        val location = geoService.getLocationFromCoordinates(lat, lon)
        return getWeatherInfos(location, lat, lon, startDate, endDate)
    }

    /**
     * 지역 이름과 날짜 범위를 이용하여 날씨 정보 리스트를 조회합니다.
     * @param lat 위도
     * @param lon 경도
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 좌표와 날짜 범위에 대한 날씨 정보 리스트
     */
    @Transactional
    fun getWeatherInfos(location: String, lat: Double, lon: Double, startDate: LocalDate, endDate: LocalDate): List<WeatherInfo> {
        // 시작 날짜와 종료 날짜 유효성 검사
        require(!startDate.isAfter(endDate)) {
            "시작 날짜($startDate)는 종료 날짜($endDate)보다 이후일 수 없습니다."
        }

        // 범위 내의 모든 날짜에 대해 날씨 정보 조회
        val result = mutableListOf<WeatherInfo>()
        var date = startDate
        while (!date.isAfter(endDate)) {
            val info = getWeatherInfo(location, lat, lon, date)
            result.add(info)
            date = date.plusDays(1)
        }
        return result
    }

    // 유효성 검사: 마지막 업데이트가 3시간 이내인지 확인
    private fun isValid(weatherInfo: WeatherInfo): Boolean {
        val lastUpdated = weatherInfo.modifyDate
        return lastUpdated.isAfter(LocalDateTime.now().minusHours(3))
    }

    // 요청된 날짜에 따라 날씨 정보를 업데이트
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
        mapDailyDataToWeatherInfo(info, matchedDaily, location, date)
        return weatherRepository.save(info)
    }

    // DailyData를 WeatherInfo로 매핑
    private fun mapDailyDataToWeatherInfo(info: WeatherInfo, data: DailyData, location: String, date: LocalDate) {
        info.weather = Weather.fromCode(data.weather.first().id)
        info.description = info.weather.description
        info.dailyTemperatureGap = (data.temp?.max ?: 0.0) - (data.temp?.min ?: 0.0)
        info.feelsLikeTemperature = data.feelsLike?.day ?: 0.0
        info.maxTemperature = data.temp?.max ?: 0.0
        info.minTemperature = data.temp?.min ?: 0.0
        info.location = location
        info.date = date
        info.pop = data.pop
        info.rain = data.rain
        info.snow = data.snow
        info.humidity = data.humidity
        info.windSpeed = data.windSpeed
        info.windDeg = data.windDeg
        info.uvi = data.uvi
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

        mapTimeMachineDataToWeatherInfo(info, data, location, date, minTemp, maxTemp)
        return weatherRepository.save(info)
    }

    // TimeMachineData를 WeatherInfo로 매핑
    private fun mapTimeMachineDataToWeatherInfo(info: WeatherInfo, data: TimeMachineData, location: String, date: LocalDate, minTemp: Double, maxTemp: Double) {
        val weather = Weather.fromCode(data.weather.first().id)
        var pop = 0.0
        val weatherCode = weather.code

        if ((weatherCode in 200 until 400) || (weatherCode in 500 until 700)) {
            pop = 1.0
        }

        info.weather = weather
        info.description = data.weather.first().description
        info.dailyTemperatureGap = maxTemp - minTemp
        info.feelsLikeTemperature = data.feelsLike
        info.maxTemperature = maxTemp
        info.minTemperature = minTemp
        info.location = location
        info.date = date
        info.pop = pop
        info.humidity = data.humidity
        info.windSpeed = data.windSpeed
        info.windDeg = data.windDeg
        info.uvi = data.uvi
    }
}
