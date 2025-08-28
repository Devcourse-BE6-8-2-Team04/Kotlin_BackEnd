package com.team04.back.infra.weather

import com.team04.back.infra.weather.dto.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class WeatherApiClientImplE2ETest @Autowired constructor(
    private val weatherApiClient: WeatherApiClient
) {

    private val TEST_LAT = 37.5665
    private val TEST_LON = 126.9780
    private val TEST_UNITS = "metric"
    private val TEST_LANG = "kr"

    @DisplayName("One Call API - Current and forecasts weather data 호출 테스트")
    @Test
    fun testFetchOneCallWeatherData() {
        val exclude = listOf("minutely", "hourly", "daily", "alerts")

        val response: OneCallApiResponse? = weatherApiClient.fetchOneCallWeatherData(
            TEST_LAT, TEST_LON, exclude, TEST_UNITS, TEST_LANG
        ).block()

        assertNotNull(response)
        assertNotNull(response.current)
        println("One Call API Response: ${response.current?.temp} ${response.current?.weather?.get(0)?.description}")
    }

    @DisplayName("One Call API - Weather data for timestamp API 호출 테스트")
    @Test
    fun testFetchTimeMachineWeatherData() {
        val dt = LocalDate.of(2023, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        val response: TimeMachineApiResponse? = weatherApiClient.fetchTimeMachineWeatherData(
            TEST_LAT, TEST_LON, dt, TEST_UNITS, TEST_LANG
        ).block()

        assertNotNull(response)
        assertNotNull(response.data)
        assertTrue(response.data.isNotEmpty())
        println("Time Machine API Response: ${response.data[0].temp} ${response.data[0].weather[0].description}")
    }

    @DisplayName("One Call API - Daily Aggregation API 호출 테스트")
    @Test
    fun testFetchDaySummaryWeatherData() {
        val date = "2023-03-30"
        val tz = "+09:00"

        val response: DaySummaryApiResponse? = weatherApiClient.fetchDaySummaryWeatherData(
            TEST_LAT, TEST_LON, date, tz, TEST_UNITS
        ).block()

        assertNotNull(response)
        assertNotNull(response.temperature)
        println("Day Summary API Response: Min Temp: ${response.temperature?.min}, Max Temp: ${response.temperature?.max}")
    }

    @DisplayName("One Call API - Weather Overview API 호출 테스트")
    @Test
    fun testFetchWeatherOverview() {
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val response: WeatherOverviewApiResponse? = weatherApiClient.fetchWeatherOverview(
            TEST_LAT, TEST_LON, date, TEST_UNITS
        ).block()

        assertNotNull(response)
        assertNotNull(response.weatherOverview)
        assertTrue(response.weatherOverview!!.isNotEmpty())
        println("Weather Overview API Response: ${response.weatherOverview}")
    }

    @DisplayName("Geocoding API - 도시 이름 → 위도/경도 조회 테스트")
    @Test
    fun testFetchCoordinatesByCity() {
        val city = "Seoul"
        val country = "KR"
        val limit = 1

        val result: List<GeoDirectResponse>? = weatherApiClient.fetchCoordinatesByCity(
            city, country, limit
        ).block()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        val geo = result[0]
        println("City to Coord: ${geo.name} → (${geo.lat}, ${geo.lon})")
    }

    @DisplayName("Geocoding API - 위도/경도 → 도시 이름 조회 테스트")
    @Test
    fun testFetchCityByCoordinates() {
        val limit = 1

        val result: List<GeoReverseResponse>? = weatherApiClient.fetchCityByCoordinates(
            TEST_LAT, TEST_LON, limit
        ).block()

        assertNotNull(result)
        assertTrue(result.isNotEmpty())
        val geo = result[0]
        println("Coord to City: (${geo.lat}, ${geo.lon}) → ${geo.name}")
    }
}
