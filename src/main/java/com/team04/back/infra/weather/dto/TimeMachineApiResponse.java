package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 과거(Time Machine) 날씨 API 응답 DTO
 */
public class TimeMachineApiResponse {
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
     * 타임머신 날씨 데이터 배열
     */
    private List<TimeMachineData> data;

    // 기본 생성자
    public TimeMachineApiResponse() {}

    // 전체 필드 생성자
    public TimeMachineApiResponse(double lat, double lon, String timezone,
                                  int timezoneOffset, List<TimeMachineData> data) {
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        this.timezoneOffset = timezoneOffset;
        this.data = data;
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public int getTimezoneOffset() { return timezoneOffset; }
    public void setTimezoneOffset(int timezoneOffset) { this.timezoneOffset = timezoneOffset; }

    public List<TimeMachineData> getData() { return data; }
    public void setData(List<TimeMachineData> data) { this.data = data; }
}
