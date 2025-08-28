package com.team04.back.domain.weather.weather.service;

import com.team04.back.common.fixture.FixtureFactory;
import com.team04.back.domain.weather.geo.service.GeoService;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;
import com.team04.back.domain.weather.weather.repository.WeatherRepository;
import com.team04.back.infra.weather.WeatherApiClient;
import com.team04.back.infra.weather.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.team04.back.common.fixture.FixtureFactory.createWeatherInfoList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * WeatherService 단위 테스트
 * WeatherService의 getWeatherInfo 및 getWeatherInfos 메서드에 대한 테스트 케이스를 포함합니다.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private WeatherApiClient weatherApiClient;

    @Mock
    private GeoService geoService;

    private double lat;
    private double lon;
    private String location;
    private LocalDate today;

    private List<WeatherInfo> weatherInfoList;
    private final String TEST_LOCATION = "Seoul";

    @BeforeEach
    void setUp() {
        lat = 37.5665;
        lon = 126.9780;
        location = "서울";
        today = LocalDate.now();
        weatherInfoList = createWeatherInfoList(TEST_LOCATION, 30);
    }

    @Test
    @DisplayName("DB에 유효한 날씨 정보가 있을 경우 DB에서 조회")
    void getWeatherInfo_ValidDataInDB_ReturnsFromDB() {
        WeatherInfo weatherInfo = FixtureFactory.createWeatherInfo(location, today, Weather.CLEAR_SKY, 20.0);
        when(weatherRepository.findByLocationAndDate(eq(location), eq(today)))
                .thenReturn(weatherInfo); // Optional 제거
        when(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location);

        WeatherInfo result = weatherService.getWeatherInfo(lat, lon, today);

        assertNotNull(result);
        assertEquals(location, result.getLocation());
        verify(weatherRepository, times(1)).findByLocationAndDate(location, today);
        verify(weatherApiClient, never()).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("DB에 날씨 정보가 없을 경우 API를 통해 조회 후 저장")
    void getWeatherInfo_NoDataInDB_FetchesFromApiAndSaves() {
        when(weatherRepository.findByLocationAndDate(eq(location), eq(today)))
                .thenReturn(null); // null 반환
        when(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location);
        when(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .thenReturn(Mono.just(createOneCallApiResponse(today)));
        when(weatherRepository.save(any(WeatherInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WeatherInfo result = weatherService.getWeatherInfo(lat, lon, today);

        assertNotNull(result);
        assertEquals(location, result.getLocation());
        assertEquals(Weather.OVERCAST_CLOUDS, result.getWeather());
        verify(weatherRepository, times(1)).findByLocationAndDate(location, today);
        verify(weatherApiClient, times(1)).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
        verify(weatherRepository, times(1)).save(any(WeatherInfo.class));
    }

    @Test
    @DisplayName("DB에 있는 날씨 정보가 오래된 경우 API를 통해 조회 후 업데이트")
    void getWeatherInfo_InvalidDataInDB_FetchesFromApiAndUpdate() {
        WeatherInfo oldWeatherInfo = FixtureFactory.createWeatherInfo(location, today, Weather.CLEAR_SKY, 20.0);
        oldWeatherInfo.setModifyDate(LocalDateTime.now().minusHours(4));
        LocalDateTime beforeUpdate = oldWeatherInfo.getModifyDate();

        when(weatherRepository.findByLocationAndDate(eq(location), eq(today)))
                .thenReturn(oldWeatherInfo);
        when(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location);
        when(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .thenReturn(Mono.just(createOneCallApiResponse(today)));
        when(weatherRepository.save(any(WeatherInfo.class))).thenAnswer(invocation -> {
            WeatherInfo saved = invocation.getArgument(0);
            saved.setModifyDate(LocalDateTime.now());
            return saved;
        });

        WeatherInfo result = weatherService.getWeatherInfo(lat, lon, today);

        assertNotNull(result);
        assertEquals(location, result.getLocation());
        assertNotEquals(beforeUpdate, result.getModifyDate());
        verify(weatherRepository, times(1)).findByLocationAndDate(location, today);
        verify(weatherApiClient, times(1)).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
        verify(weatherRepository, times(1)).save(any(WeatherInfo.class));
    }

    @Test
    @DisplayName("여러 날짜에 대한 날씨 정보 조회")
    void getWeatherInfos_DateRange_ReturnsWeatherInfoList() {
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(2);

        when(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location);
        when(weatherRepository.findByLocationAndDate(anyString(), any(LocalDate.class)))
                .thenReturn(null);
        when(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .thenReturn(Mono.just(createOneCallApiResponse(startDate, endDate)));
        when(weatherRepository.save(any(WeatherInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<WeatherInfo> results = weatherService.getWeatherInfos(lat, lon, startDate, endDate);

        assertEquals(3, results.size());
        verify(weatherRepository, times(3)).findByLocationAndDate(anyString(), any(LocalDate.class));
        verify(weatherApiClient, times(3)).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
        verify(weatherRepository, times(3)).save(any(WeatherInfo.class));
    }

    @Test
    @DisplayName("잘못된 날짜 범위로 조회 시 예외 발생")
    void getWeatherInfos_InvalidDateRange_ThrowsException() {
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today;

        when(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location);

        assertThrows(IllegalArgumentException.class, () -> weatherService.getWeatherInfos(lat, lon, startDate, endDate));
    }

    @Test
    @DisplayName("API 응답에 해당 날짜 데이터가 없을 경우 예외 발생")
    void getWeatherInfo_ApiNoDataForDate_ThrowsException() {
        LocalDate requestDate = today.plusDays(5);
        when(weatherRepository.findByLocationAndDate(eq(location), eq(requestDate))).thenReturn(null);
        when(geoService.getLocationFromCoordinates(lat, lon)).thenReturn(location);
        when(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .thenReturn(Mono.just(createOneCallApiResponse(today, today.plusDays(4))));

        assertThrows(IllegalArgumentException.class, () -> weatherService.getWeatherInfo(lat, lon, requestDate));
    }

    @Test
    @DisplayName("지역과, 시작, 종료일이 주어지면 시작, 종료일을 포함한 날씨 정보를 반환")
    void getWeatherByDuration_success() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);

        List<WeatherInfo> expectedWeatherInfos = weatherInfoList.stream()
                .filter(wi -> !wi.getDate().isBefore(start) && !wi.getDate().isAfter(end))
                .collect(Collectors.toList());

        when(weatherRepository.findByLocationAndDateBetween(eq(TEST_LOCATION), eq(start), eq(end)))
                .thenReturn(expectedWeatherInfos);

        List<WeatherInfo> result = weatherService.getDurationWeather(TEST_LOCATION, start, end);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(expectedWeatherInfos.size());
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWeatherInfos);
    }

    // Helper
    private OneCallApiResponse createOneCallApiResponse(LocalDate date) {
        return createOneCallApiResponse(date, date);
    }

    private OneCallApiResponse createOneCallApiResponse(LocalDate startDate, LocalDate endDate) {
        List<DailyData> dailyDataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyData dailyData = new DailyData(
                    date.atStartOfDay().toEpochSecond(ZoneOffset.UTC), // dt
                    0, // sunrise
                    0, // sunset
                    0, // moonrise
                    0, // moonset
                    0.0, // moonPhase
                    null, // summary
                    new DailyTemp(25.0, 15.0, 25.0, 15.0, 20.0, 15.0), // temp
                    new DailyFeelsLike(20.0, 20.0, 20.0, 20.0), // feelsLike
                    0, // pressure
                    0, // humidity
                    0.0, // dewPoint
                    0.0, // windSpeed
                    0,   // windDeg
                    0.0, // windGust
                    Collections.singletonList(
                            new WeatherDescription(804, "Overcast Cloud", "overcast clouds", "04d")
                    ), // weather
                    0, // clouds
                    0.0, // pop
                    0.0, // rain
                    0.0, // snow
                    0.0  // uvi
            );
            dailyDataList.add(dailyData);
        }

        return new OneCallApiResponse(
                0.0, // lat
                0.0, // lon
                null, // timezone
                0,    // timezoneOffset
                null, // current
                Collections.emptyList(), // minutely
                Collections.emptyList(), // hourly
                dailyDataList,           // daily
                Collections.emptyList()  // alerts
        );
    }

}