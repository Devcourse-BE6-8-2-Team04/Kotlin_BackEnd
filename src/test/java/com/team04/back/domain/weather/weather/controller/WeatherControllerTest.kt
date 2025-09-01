package com.team04.back.domain.weather.weather.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * WeatherController 통합 테스트
 * WeatherController의 API 엔드포인트를 통합적으로 검증합니다.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WeatherControllerTest @Autowired constructor(
    private val mvc: MockMvc
) {

    private lateinit var latStr: String
    private lateinit var lonStr: String

    @BeforeEach
    fun setUp() {
        latStr = "37.5665"
        lonStr = "126.9780"
    }

    @Test
    @DisplayName("주간 날씨 조회 성공")
    fun getWeeklyWeather_success() {
        // when
        val result = mvc.perform(
            get("/api/v1/weathers")
                .param("lat", latStr)
                .param("lon", lonStr)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(7))
            .andExpect(jsonPath("$[0].location").value("서울"))
    }

    @Test
    @DisplayName("주간 날씨 조회 성공 (location 파라미터 포함)")
    fun getWeeklyWeather_withLocation_success() {
        val result = mvc.perform(
            get("/api/v1/weathers")
                .param("location", "서울시")
                .param("lat", latStr)
                .param("lon", lonStr)
        )

        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(7))
            .andExpect(jsonPath("$[0].location").value("서울"))
    }

    @Test
    @DisplayName("특정 날짜 날씨 조회 성공")
    fun getWeatherByDate_success() {
        // given
        val today = LocalDate.now()

        // when
        val result = mvc.perform(
            get("/api/v1/weathers/{date}", today)
                .param("lat", latStr)
                .param("lon", lonStr)
        )

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.date").value(today.toString()))
            .andExpect(jsonPath("$.weather").exists())
            .andExpect(jsonPath("$.location").value("서울"))
    }

    @Test
    @DisplayName("위도 또는 경도 누락 시 400 Bad Request")
    fun missingLatLonParams_returns400() {
        // when
        val noLon = mvc.perform(get("/api/v1/weathers").param("lat", latStr))
        val noLat = mvc.perform(get("/api/v1/weathers").param("lon", lonStr))

        // then
        noLon.andExpect(status().isBadRequest)
        noLat.andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("날짜 포맷이 잘못된 경우 400 Bad Request")
    fun invalidDateFormat_returns400() {
        // when
        val result = mvc.perform(
            get("/api/v1/weathers/2024-13-99")
                .param("lat", latStr)
                .param("lon", lonStr)
        )

        // then
        result.andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.msg").exists())
    }

    @Test
    @DisplayName("범위 외 날짜 요청 시 400 Bad Request + 예외 메시지 확인")
    fun dateOutOfRange_returnsError() {
        // given
        val tooFar = LocalDate.now().plusDays(100)
        val expectedMessage = "해당 날짜($tooFar)에 대한 예보 데이터가 존재하지 않습니다."

        // when
        val result = mvc.perform(
            get("/api/v1/weathers/{date}", tooFar)
                .param("lat", latStr)
                .param("lon", lonStr)
        )

        // then
        result.andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.msg").value(expectedMessage))
    }
}
