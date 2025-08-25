package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherOverviewApiResponse {
    /**
     * 위도, 십진수 (-90; 90)
     */
    private double lat;
    /**
     * 경도, 십진수 (-180; 180)
     */
    private double lon;
    /**
     * 시간대 (±XX:XX 형식)
     */
    private String tz;
    /**
     * 요약이 생성된 날짜 (YYYY-MM-DD 형식)
     */
    private String date;
    /**
     * 요청에 지정된 측정 단위
     */
    private String units;
    /**
     * AI가 생성한 요청된 날짜의 날씨 개요
     */
    @JsonProperty("weather_overview")
    private String weatherOverview;
}
