package com.team04.back.infra.weather.dto;

/**
 * 날씨 상태 설명 데이터
 */
public class WeatherDescription {
    private int id;
    private String main;
    private String description;
    private String icon;

    // 기본 생성자
    public WeatherDescription() {}

    // 전체 필드 생성자
    public WeatherDescription(int id, String main, String description, String icon) {
        this.id = id;
        this.main = main;
        this.description = description;
        this.icon = icon;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMain() { return main; }
    public void setMain(String main) { this.main = main; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
