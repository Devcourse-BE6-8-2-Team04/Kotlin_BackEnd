package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OneCallApiResponse {
    /**
     * 위도, 십진수 (-90; 90)
     */
    private double lat;
    /**
     * 경도, 십진수 (-180; 180)
     */
    private double lon;
    /**
     * 요청된 위치의 시간대 이름
     */
    private String timezone;
    /**
     * UTC로부터의 시간 오프셋 (초)
     */
    @JsonProperty("timezone_offset")
    private int timezoneOffset;
    /**
     * 현재 날씨 데이터
     */
    private CurrentWeather current;
    /**
     * 1시간 동안의 분 단위 예보 데이터
     */
    private List<MinutelyData> minutely;
    /**
     * 48시간 동안의 시간 단위 예보 데이터
     */
    private List<HourlyData> hourly;
    /**
     * 8일 동안의 일 단위 예보 데이터
     */
    private List<DailyData> daily;
    /**
     * 국가 날씨 경보 데이터
     */
    private List<AlertData> alerts;
}
