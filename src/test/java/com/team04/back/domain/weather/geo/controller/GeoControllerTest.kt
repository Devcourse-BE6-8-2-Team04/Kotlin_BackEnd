package com.team04.back.domain.weather.geo.controller

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * GeoController 통합 테스트
 * GeoController의 API 엔드포인트를 통합적으로 검증합니다.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class GeoControllerTest @Autowired constructor(
    private val mvc: MockMvc
) {

    @Test
    @DisplayName("도시 이름으로 좌표 조회 성공")
    fun getGeoLocations_success() {
        // when
        val result = mvc.perform(get("/api/v1/geos")
            .param("location", "서울"))

        // then
        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Seoul"))
            .andExpect(jsonPath("$[0].lat").exists())
            .andExpect(jsonPath("$[0].lon").exists())
            .andExpect(jsonPath("$[0].country").value("KR"))
            .andExpect(jsonPath("$[0].localName").value("서울"))
    }

    @Test
    @DisplayName("도시 이름 누락 시 400 Bad Request")
    fun missingLocation_returns400() {
        // when
        val result = mvc.perform(get("/api/v1/geos")) // location 생략

        // then
        result.andExpect(status().isBadRequest)
    }
}
