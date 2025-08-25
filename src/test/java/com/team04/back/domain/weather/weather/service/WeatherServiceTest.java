package com.team04.back.domain.weather.weather.service;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.team04.back.common.fixture.FixtureFactory.createWeatherInfoList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
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
        // Given
        WeatherInfo weatherInfo = createWeatherInfo(today, LocalDateTime.now());
        given(weatherRepository.findByLocationAndDate(location, today)).willReturn(Optional.of(weatherInfo));
        given(geoService.getLocationFromCoordinates(lat, lon)).willReturn(location);

        // When
        WeatherInfo result = weatherService.getWeatherInfo(lat, lon, today);

        // Then
        assertNotNull(result);
        assertEquals(location, result.getLocation());
        verify(weatherRepository, times(1)).findByLocationAndDate(location, today);
        verify(weatherApiClient, never()).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("DB에 날씨 정보가 없을 경우 API를 통해 조회 후 저장")
    void getWeatherInfo_NoDataInDB_FetchesFromApiAndSaves() {
        // Given
        given(weatherRepository.findByLocationAndDate(location, today)).willReturn(Optional.empty());
        given(geoService.getLocationFromCoordinates(lat, lon)).willReturn(location);
        given(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .willReturn(Mono.just(createOneCallApiResponse(today)));
        given(weatherRepository.save(any(WeatherInfo.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        WeatherInfo result = weatherService.getWeatherInfo(lat, lon, today);

        // Then
        assertNotNull(result);
        assertEquals(location, result.getLocation());
        assertEquals(Weather.OVERCAST_CLOUDS, result.getWeather());
        verify(weatherRepository, times(1)).findByLocationAndDate(location, today);
        verify(weatherApiClient, times(1)).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
        verify(weatherRepository, times(1)).save(any(WeatherInfo.class));
    }

    @Test
    @DisplayName("DB에 있는 날씨 정보가 유효하지 않은(오래된) 경우 API를 통해 조회 후 업데이트")
    void getWeatherInfo_InvalidDataInDB_FetchesFromApiAndUpdate() {
        // Given
        WeatherInfo oldWeatherInfo = createWeatherInfo(today, LocalDateTime.now().minusHours(4));
        LocalDateTime beforeUpdate = oldWeatherInfo.getModifyDate();
        given(weatherRepository.findByLocationAndDate(location, today)).willReturn(Optional.of(oldWeatherInfo));
        given(geoService.getLocationFromCoordinates(lat, lon)).willReturn(location);
        given(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .willReturn(Mono.just(createOneCallApiResponse(today)));
        given(weatherRepository.save(any(WeatherInfo.class))).willAnswer(invocation -> {
            WeatherInfo savedInfo = invocation.getArgument(0);
            savedInfo.setModifyDate(LocalDateTime.now()); // Simulate update
            return savedInfo;
        });

        // When
        WeatherInfo result = weatherService.getWeatherInfo(lat, lon, today);

        // Then
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
        // Given
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(2);
        given(geoService.getLocationFromCoordinates(lat, lon)).willReturn(location);
        given(weatherRepository.findByLocationAndDate(anyString(), any(LocalDate.class))).willReturn(Optional.empty());
        given(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .willReturn(Mono.just(createOneCallApiResponse(startDate, endDate)));
        given(weatherRepository.save(any(WeatherInfo.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        List<WeatherInfo> results = weatherService.getWeatherInfos(lat, lon, startDate, endDate);

        // Then
        assertEquals(3, results.size());
        verify(weatherRepository, times(3)).findByLocationAndDate(anyString(), any(LocalDate.class));
        verify(weatherApiClient, times(3)).fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString());
        verify(weatherRepository, times(3)).save(any(WeatherInfo.class));
    }

    @Test
    @DisplayName("잘못된 날짜 범위로 조회 시 예외 발생")
    void getWeatherInfos_InvalidDateRange_ThrowsException() {
        // Given
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today;
        given(geoService.getLocationFromCoordinates(lat, lon)).willReturn(location);


        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeatherInfos(lat, lon, startDate, endDate);
        });
    }

    @Test
    @DisplayName("API 응답에 해당 날짜 데이터가 없을 경우 예외 발생")
    void getWeatherInfo_ApiNoDataForDate_ThrowsException() {
        // Given
        LocalDate requestDate = today.plusDays(5);
        given(weatherRepository.findByLocationAndDate(location, requestDate)).willReturn(Optional.empty());
        given(geoService.getLocationFromCoordinates(lat, lon)).willReturn(location);
        // Return API response that does not contain the requestDate
        given(weatherApiClient.fetchOneCallWeatherData(anyDouble(), anyDouble(), any(), anyString(), anyString()))
                .willReturn(Mono.just(createOneCallApiResponse(today, today.plusDays(4))));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeatherInfo(lat, lon, requestDate);
        });
    }

    // 날씨 정보를 생성하는 헬퍼 메서드
    private WeatherInfo createWeatherInfo(LocalDate date, LocalDateTime modifiedDate) {
        return WeatherInfo.builder()
                .location(location)
                .date(date)
                .weather(Weather.CLEAR_SKY)
                .minTemperature(15.0)
                .maxTemperature(25.0)
                .dailyTemperatureGap(10.0)
                .feelsLikeTemperature(20.0)
                .createDate(LocalDateTime.now())
                .modifyDate(modifiedDate)
                .build();
    }

    // GeoReverseResponse 객체를 생성하는 헬퍼 메서드
    private GeoReverseResponse createGeoReverseResponse() {
        GeoReverseResponse response = new GeoReverseResponse();
        response.setName(location);
        response.setLat(lat);
        response.setLon(lon);
        response.setCountry("KR");
        response.setState("서울");
        response.setLocalNames(new GeoDirectResponse.LocalNames("서울", "Seoul", "ソウル"));
        return response;
    }

    // OneCallApiResponse 객체를 생성하는 헬퍼 메서드
    private OneCallApiResponse createOneCallApiResponse(LocalDate date) {
        return createOneCallApiResponse(date, date);
    }

    // OneCallApiResponse 객체를 생성하는 헬퍼 메서드
    private OneCallApiResponse createOneCallApiResponse(LocalDate startDate, LocalDate endDate) {
        OneCallApiResponse response = new OneCallApiResponse();
        List<DailyData> dailyDataList = new java.util.ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyData dailyData = new DailyData();
            dailyData.setDt(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
            dailyData.setTemp(new DailyTemp(25.0, 15.0, 25.0, 15.0, 20.0, 15.0));
            dailyData.setFeelsLike(new DailyFeelsLike(20.0, 20.0, 20.0, 20.0));
            dailyData.setWeather(Collections.singletonList(new WeatherDescription(804, "Overcast Cloud", "overcast clouds", "04d")));
            dailyDataList.add(dailyData);
        }
        response.setDaily(dailyDataList);
        return response;
    }

    @Test
    @DisplayName("지역과, 시작, 종료일이 주어지면 시작, 종료일을 포함한 날씨 정보를 반환한다.")
    void getWeatherByDuration_success() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);

        List<WeatherInfo> expectedWeatherInfos = weatherInfoList.stream()
                .filter(wi -> !wi.getDate().isBefore(start) && !wi.getDate().isAfter(end))
                .collect(Collectors.toList());

        when(weatherRepository.findByLocationAndDateBetween(
                eq(TEST_LOCATION),
                eq(start),
                eq(end))
        ).thenReturn(expectedWeatherInfos);

        List<WeatherInfo> result = weatherService.getDurationWeather(TEST_LOCATION, start, end);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(expectedWeatherInfos.size());
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWeatherInfos);
    }
}