package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 좌표 → 지역명 응답 DTO
 */
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

    // 기본 생성자
    public GeoReverseResponse() {}

    // 전체 필드 생성자
    public GeoReverseResponse(String name, double lat, double lon, String country, String state, GeoDirectResponse.LocalNames localNames) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.country = country;
        this.state = state;
        this.localNames = localNames;
    }

    // Getter & Setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public GeoDirectResponse.LocalNames getLocalNames() {
        return localNames;
    }
    public void setLocalNames(GeoDirectResponse.LocalNames localNames) {
        this.localNames = localNames;
    }
}
