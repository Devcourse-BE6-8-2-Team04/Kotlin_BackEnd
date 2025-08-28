package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 좌표 → 지역명 응답 DTO
 */
data class GeoReverseResponse(

    /** 도시 이름 (예: Seoul) */
    val name: String? = null,

    /** 위도, 십진수 (-90; 90) */
    val lat: Double = 0.0,

    /** 경도, 십진수 (-180; 180) */
    val lon: Double = 0.0,

    /** 국가 코드 (예: KR, US) */
    val country: String? = null,

    /** (선택 사항) 주/도 이름 */
    val state: String? = null,

    /** 지역 이름 목록 (현지어 이름 등) */
    @JsonProperty("local_names")
    val localNames: GeoDirectResponse.LocalNames? = null  // GeoDirectResponse 내부 클래스 재활용
)
