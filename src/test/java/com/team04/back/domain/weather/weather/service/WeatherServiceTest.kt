package com.team04.back.domain.weather.weather.service

import com.team04.back.common.fixture.FixtureFactory
import com.team04.back.common.fixture.FixtureFactory.createWeatherInfoList
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.domain.weather.weather.repository.WeatherRepository
import com.team04.back.infra.weather.WeatherApiClient
import com.team04.back.infra.weather.dto.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * WeatherService 단위 테스트
 * WeatherService의 getWeatherInfo 및 getWeatherInfos 메서드에 대한 테스트 케이스를 포함합니다.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class WeatherServiceTest {

    @InjectMocks
    private lateinit var weatherService: WeatherService

    private lateinit var spyWeatherService: WeatherService

    @Mock
    private lateinit var weatherRepository: WeatherRepository

    @Mock
    private lateinit var weatherApiClient: WeatherApiClient

    @Mock
    private lateinit var geoService: GeoService

    private var lat = 37.5665
    private var lon = 126.9780
    private val location = "서울"
    private lateinit var today: LocalDate
    private lateinit var weatherInfoList: List<WeatherInfo>

    private val TEST_LOCATION = "Seoul"

    @BeforeEach
    fun setUp() {
        today = LocalDate.now()
        weatherInfoList = createWeatherInfoList(TEST_LOCATION, 30)
    }

    @BeforeEach
    fun initSpy() {
        spyWeatherService = spy(weatherService)
    }

    @Test
    @DisplayName("location이 unknown일 경우 주간 날씨 정보 조회")
    fun getWeeklyWeather_LocationUnknown_CallsGetWeatherInfosWithCoordinates() {
        val startDate = today
        val endDate = today.plusDays(6)
        val expected = weatherInfoList.take(7)

        doReturn(expected).`when`(spyWeatherService).getWeatherInfos(lat, lon, startDate, endDate)

        val result = spyWeatherService.getWeeklyWeather("unknown", lat, lon)

        assertThat(result).isEqualTo(expected)
        verify(geoService, never()).normalizeCityName(any(), any(), any())
    }

    @Test
    @DisplayName("location이 주어질 경우 정규화 후 주간 날씨 정보 조회")
    fun getWeeklyWeather_WithLocation_NormalizesAndCallsGetWeatherInfos() {
        val startDate = today
        val endDate = today.plusDays(6)
        val normalized = "Seoul"
        val expected = weatherInfoList.take(7)

        whenever(geoService.normalizeCityName(location, lat, lon)).thenReturn(normalized)
        doReturn(expected).`when`(spyWeatherService).getWeatherInfos(normalized, lat, lon, startDate, endDate)

        val result = spyWeatherService.getWeeklyWeather(location, lat, lon)

        assertThat(result).isEqualTo(expected)
        verify(geoService).normalizeCityName(eq(location), eq(lat), eq(lon))
    }

    @Test
    @DisplayName("DB에 유효한 날씨 정보가 있을 경우 DB에서 조회")
    fun getWeatherInfo_ValidDataInDB_ReturnsFromDB() {
        val weatherInfo = FixtureFactory.createWeatherInfo(location, today, Weather.CLEAR_SKY, 20.0)
        whenever(weatherRepository.findByLocationAndDate(eq(location), eq(today)))
            .thenReturn(weatherInfo)
        whenever(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location)

        val result = weatherService.getWeatherInfo(lat, lon, today)

        assertThat(result).isNotNull
        assertThat(result.location).isEqualTo(location)
        verify(weatherRepository).findByLocationAndDate(eq(location), eq(today))
        verify(weatherApiClient, never()).fetchOneCallWeatherData(any(), any(), any(), any(), any())
    }

    @Test
    @DisplayName("DB에 날씨 정보가 없을 경우 API를 통해 조회 후 저장")
    fun getWeatherInfo_NoDataInDB_FetchesFromApiAndSaves() {
        whenever(weatherRepository.findByLocationAndDate(eq(location), eq(today))).thenReturn(null)
        whenever(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location)
        whenever(weatherApiClient.fetchOneCallWeatherData(any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(createOneCallApiResponse(today)))
        whenever(weatherRepository.save(any<WeatherInfo>())).thenAnswer { it.arguments[0] }


        val result = weatherService.getWeatherInfo(lat, lon, today)

        assertThat(result).isNotNull
        assertThat(result.location).isEqualTo(location)
        assertThat(result.weather).isEqualTo(Weather.OVERCAST_CLOUDS)
        verify(weatherRepository).findByLocationAndDate(eq(location), eq(today))
        verify(weatherApiClient).fetchOneCallWeatherData(any(), any(), any(), any(), any())
        verify(weatherRepository).save(any())
    }

    @Test
    @DisplayName("DB에 있는 날씨 정보가 오래된 경우 API를 통해 조회 후 업데이트")
    fun getWeatherInfo_InvalidDataInDB_FetchesFromApiAndUpdate() {
        val oldWeatherInfo = FixtureFactory.createWeatherInfo(location, today, Weather.CLEAR_SKY, 20.0).apply {
            modifyDate = LocalDateTime.now().minusHours(4)
        }
        val beforeUpdate = oldWeatherInfo.modifyDate

        whenever(weatherRepository.findByLocationAndDate(eq(location), eq(today))).thenReturn(oldWeatherInfo)
        whenever(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location)
        whenever(weatherApiClient.fetchOneCallWeatherData(any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(createOneCallApiResponse(today)))
        whenever(weatherRepository.save(any<WeatherInfo>())).thenAnswer {
            (it.arguments[0] as WeatherInfo).apply { modifyDate = LocalDateTime.now() }
        }

        val result = weatherService.getWeatherInfo(lat, lon, today)

        assertThat(result).isNotNull
        assertThat(result.location).isEqualTo(location)
        assertThat(result.modifyDate).isNotEqualTo(beforeUpdate)
        verify(weatherRepository).findByLocationAndDate(eq(location), eq(today))
        verify(weatherApiClient).fetchOneCallWeatherData(any(), any(), any(), any(), any())
        verify(weatherRepository).save(any())
    }

    @Test
    @DisplayName("여러 날짜에 대한 날씨 정보 조회")
    fun getWeatherInfos_DateRange_ReturnsWeatherInfoList() {
        val startDate = today
        val endDate = today.plusDays(2)

        whenever(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location)
        whenever(weatherRepository.findByLocationAndDate(any(), any())).thenReturn(null)
        whenever(weatherApiClient.fetchOneCallWeatherData(any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(createOneCallApiResponse(startDate, endDate)))
        whenever(weatherRepository.save(any<WeatherInfo>())).thenAnswer { it.arguments[0] }

        val results = weatherService.getWeatherInfos(lat, lon, startDate, endDate)

        assertThat(results).hasSize(3)
        verify(weatherRepository, times(3)).findByLocationAndDate(any(), any())
        verify(weatherApiClient, times(3)).fetchOneCallWeatherData(any(), any(), any(), any(), any())
        verify(weatherRepository, times(3)).save(any())
    }

    @Test
    @DisplayName("잘못된 날짜 범위로 조회 시 예외 발생")
    fun getWeatherInfos_InvalidDateRange_ThrowsException() {
        val startDate = today.plusDays(1)
        val endDate = today

        whenever(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location)

        assertThrows<IllegalArgumentException> {
            weatherService.getWeatherInfos(lat, lon, startDate, endDate)
        }
    }

    @Test
    @DisplayName("API 응답에 해당 날짜 데이터가 없을 경우 예외 발생")
    fun getWeatherInfo_ApiNoDataForDate_ThrowsException() {
        val requestDate = today.plusDays(5)
        whenever(weatherRepository.findByLocationAndDate(eq(location), eq(requestDate))).thenReturn(null)
        whenever(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location)
        whenever(weatherApiClient.fetchOneCallWeatherData(any(), any(), any(), any(), any()))
            .thenReturn(Mono.just(createOneCallApiResponse(today, today.plusDays(4))))

        assertThrows<IllegalArgumentException> {
            weatherService.getWeatherInfo(lat, lon, requestDate)
        }
    }

    @Test
    @DisplayName("지역과, 시작, 종료일이 주어지면 시작, 종료일을 포함한 날씨 정보를 반환")
    fun getWeatherByDuration_success() {
        val start = LocalDate.now()
        val end = LocalDate.now().plusDays(7)

        val expectedWeatherInfos = weatherInfoList.filter { it.date in start..end }

        whenever(weatherRepository.findByLocationAndDateBetween(TEST_LOCATION, start, end))
            .thenReturn(expectedWeatherInfos)

        val result = weatherService.getDurationWeather(TEST_LOCATION, start, end)

        assertThat(result).isNotNull
        assertThat(result).hasSize(expectedWeatherInfos.size)
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWeatherInfos)
    }

    // Helper
    private fun createOneCallApiResponse(date: LocalDate): OneCallApiResponse =
        createOneCallApiResponse(date, date)

    private fun createOneCallApiResponse(startDate: LocalDate, endDate: LocalDate): OneCallApiResponse {
        val dailyDataList = generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .map { date ->
                DailyData(
                    dt = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC),
                    temp = DailyTemp(25.0, 15.0, 25.0, 15.0, 20.0, 15.0),
                    feelsLike = DailyFeelsLike(20.0, 20.0, 20.0, 20.0),
                    weather = listOf(
                        WeatherDescription(804, "Overcast Cloud", "overcast clouds", "04d")
                    )
                )
            }.toList()

        return OneCallApiResponse(daily = dailyDataList)
    }
}
