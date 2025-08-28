package com.team04.back.infra.weather.dto;

/**
 * 체감 온도 데이터
 */
public class DailyFeelsLike {
    private double day;
    private double night;
    private double eve;
    private double morn;

    // 기본 생성자
    public DailyFeelsLike() {}

    // 전체 필드 생성자
    public DailyFeelsLike(double day, double night, double eve, double morn) {
        this.day = day;
        this.night = night;
        this.eve = eve;
        this.morn = morn;
    }

    public double getDay() { return day; }
    public void setDay(double day) { this.day = day; }

    public double getNight() { return night; }
    public void setNight(double night) { this.night = night; }

    public double getEve() { return eve; }
    public void setEve(double eve) { this.eve = eve; }

    public double getMorn() { return morn; }
    public void setMorn(double morn) { this.morn = morn; }
}
