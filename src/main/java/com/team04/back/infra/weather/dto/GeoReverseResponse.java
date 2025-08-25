package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoReverseResponse {

    /** 도시 이름 (예: Seoul) */
    private String name;

    /** 위도, 십진수 (-90; 90) */
    private double lat;

    /** 경도, 십진수 (-180; 180) */
    private double lon;

    /** 국가 코드 (예: KR, US) */
    private String country;

    /** (선택 사항) 주/도 이름 */
    private String state;

    /** 지역 이름 목록 (현지어 이름 등) */
    @JsonProperty("local_names")
    private GeoDirectResponse.LocalNames localNames;  // GeoDirectResponse 내부 클래스 재활용
}
