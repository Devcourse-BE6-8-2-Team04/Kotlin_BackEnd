package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 도시 위치 응답 DTO
 */
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

    // 기본 생성자
    public GeoDirectResponse() {}

    // 전체 필드 생성자
    public GeoDirectResponse(String name, double lat, double lon, String country, String state, LocalNames localNames) {
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

    public LocalNames getLocalNames() {
        return localNames;
    }
    public void setLocalNames(LocalNames localNames) {
        this.localNames = localNames;
    }

    /**
     * 지역 이름 목록 (예: 한국어, 영어, 일본어)
     */
    public static class LocalNames {
        @JsonProperty("ko")
        private String korean;

        @JsonProperty("en")
        private String english;

        @JsonProperty("ja")
        private String japanese;

        // 기본 생성자
        public LocalNames() {}

        // 전체 필드 생성자
        public LocalNames(String korean, String english, String japanese) {
            this.korean = korean;
            this.english = english;
            this.japanese = japanese;
        }

        // Getter & Setter
        public String getKorean() {
            return korean;
        }
        public void setKorean(String korean) {
            this.korean = korean;
        }

        public String getEnglish() {
            return english;
        }
        public void setEnglish(String english) {
            this.english = english;
        }

        public String getJapanese() {
            return japanese;
        }
        public void setJapanese(String japanese) {
            this.japanese = japanese;
        }
    }
}
