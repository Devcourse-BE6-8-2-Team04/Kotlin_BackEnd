package com.team04.back.domain.weather.weather.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WeatherController 통합 테스트
 * WeatherController의 API 엔드포인트를 통합적으로 검증합니다.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WeatherControllerTest {

    @Autowired
    private MockMvc mvc;

    private String latStr;
    private String lonStr;

    @BeforeEach
    void setUp() {
        latStr = "37.5665";
        lonStr = "126.9780";
    }

    @Test
    @DisplayName("주간 날씨 조회 성공")
    void getWeeklyWeather_success() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/api/v1/weathers")
                .param("lat", latStr)
                .param("lon", lonStr));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[0].location").value("서울"));
    }

    @Test
    @DisplayName("특정 날짜 날씨 조회 성공")
    void getWeatherByDate_success() throws Exception {
        // given
        LocalDate today = LocalDate.now();

        // when
        ResultActions result = mvc.perform(get("/api/v1/weathers/{date}", today)
                .param("lat", latStr)
                .param("lon", lonStr));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(today.toString()))
                .andExpect(jsonPath("$.weather").exists())
                .andExpect(jsonPath("$.location").value("서울"));
    }

    @Test
    @DisplayName("위도 또는 경도 누락 시 400 Bad Request")
    void missingLatLonParams_returns400() throws Exception {
        // when
        ResultActions noLon = mvc.perform(get("/api/v1/weathers")
                .param("lat", latStr));
        ResultActions noLat = mvc.perform(get("/api/v1/weathers")
                .param("lon", lonStr));

        // then
        noLon.andExpect(status().isBadRequest());
        noLat.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("날짜 포맷이 잘못된 경우 400 Bad Request")
    void invalidDateFormat_returns400() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/api/v1/weathers/2024-13-99")
                .param("lat", latStr)
                .param("lon", lonStr));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    @DisplayName("범위 외 날짜 요청 시 400 Bad Request + 예외 메시지 확인")
    void dateOutOfRange_returnsError() throws Exception {
        // given
        LocalDate tooFar = LocalDate.now().plusDays(100);
        String expectedMessage = "해당 날짜(" + tooFar + ")에 대한 예보 데이터가 존재하지 않습니다.";

        // when
        ResultActions result = mvc.perform(get("/api/v1/weathers/{date}", tooFar)
                .param("lat", latStr)
                .param("lon", lonStr));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value(expectedMessage));
    }
}
