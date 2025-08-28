package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * OpenWeather One Call API 응답 DTO
 */
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

    // 기본 생성자
    public OneCallApiResponse() {}

    // 전체 필드 생성자
    public OneCallApiResponse(double lat, double lon, String timezone, int timezoneOffset,
                              CurrentWeather current, List<MinutelyData> minutely,
                              List<HourlyData> hourly, List<DailyData> daily,
                              List<AlertData> alerts) {
        this.lat = lat;
        this.lon = lon;
        this.timezone = timezone;
        this.timezoneOffset = timezoneOffset;
        this.current = current;
        this.minutely = minutely;
        this.hourly = hourly;
        this.daily = daily;
        this.alerts = alerts;
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public int getTimezoneOffset() { return timezoneOffset; }
    public void setTimezoneOffset(int timezoneOffset) { this.timezoneOffset = timezoneOffset; }

    public CurrentWeather getCurrent() { return current; }
    public void setCurrent(CurrentWeather current) { this.current = current; }

    public List<MinutelyData> getMinutely() { return minutely; }
    public void setMinutely(List<MinutelyData> minutely) { this.minutely = minutely; }

    public List<HourlyData> getHourly() { return hourly; }
    public void setHourly(List<HourlyData> hourly) { this.hourly = hourly; }

    public List<DailyData> getDaily() { return daily; }
    public void setDaily(List<DailyData> daily) { this.daily = daily; }

    public List<AlertData> getAlerts() { return alerts; }
    public void setAlerts(List<AlertData> alerts) { this.alerts = alerts; }
}
