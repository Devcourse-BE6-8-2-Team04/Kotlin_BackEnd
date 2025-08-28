package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 도시 위치 응답 DTO
 */
data class GeoDirectResponse(

    /** 도시 이름 (예: Seoul) */
    val name: String? = null,

    /** 위도 */
    val lat: Double = 0.0,

    /** 경도 */
    val lon: Double = 0.0,

    /** 국가 코드 (예: KR, US) */
    val country: String? = null,

    /** (선택적) 상태 */
    val state: String? = null,

    /** 지역 이름 목록 (예: 한국어, 일본어 등 현지어) */
    @JsonProperty("local_names")
    val localNames: LocalNames? = null
) {
    /**
     * 지역 이름 목록 (예: 한국어, 영어, 일본어)
     */
    data class LocalNames(
        @JsonProperty("ko")
        val korean: String? = null,

        @JsonProperty("en")
        val english: String? = null,

        @JsonProperty("ja")
        val japanese: String? = null
    )
}
