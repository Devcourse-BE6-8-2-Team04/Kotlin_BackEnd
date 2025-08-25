package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoDirectResponse {

    /** 도시 이름 (예: Seoul) */
    private String name;

    /** 위도 */
    private double lat;

    /** 경도 */
    private double lon;

    /** 국가 코드 (예: KR, US) */
    private String country;

    /** (선택적) 상태 */
    private String state;

    /** 지역 이름 목록 (예: 한국어, 일본어 등 현지어) */
    @JsonProperty("local_names")
    private LocalNames localNames;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocalNames {
        @JsonProperty("ko")
        private String korean;

        @JsonProperty("en")
        private String english;

        @JsonProperty("ja")
        private String japanese;

        // 필요에 따라 더 추가 가능
    }
}
