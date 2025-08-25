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
public class DaySummaryApiResponse {
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
     * API 요청에 지정된 날짜 (YYYY-MM-DD 형식)
     */
    private String date;
    /**
     * 요청에 지정된 측정 단위
     */
    private String units;
    /**
     * 구름 관련 정보
     */
    @JsonProperty("cloud_cover")
    private CloudCover cloudCover;
    /**
     * 습도 관련 정보
     */
    private HumiditySummary humidity;
    /**
     * 강수량 관련 정보
     */
    private PrecipitationSummary precipitation;
    /**
     * 온도 관련 정보
     */
    private TemperatureSummary temperature;
    /**
     * 기압 관련 정보
     */
    private PressureSummary pressure;
    /**
     * 바람 관련 정보
     */
    private WindSummary wind;
}
