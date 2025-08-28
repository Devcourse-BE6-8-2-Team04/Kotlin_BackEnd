package com.team04.back.infra.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 일 단위 날씨 데이터
 */
public class DailyData {
    private long dt;
    private long sunrise;
    private long sunset;
    private long moonrise;
    private long moonset;
    @JsonProperty("moon_phase")
    private double moonPhase;
    private String summary;
    private DailyTemp temp;
    @JsonProperty("feels_like")
    private DailyFeelsLike feelsLike;
    private int pressure;
    private int humidity;
    @JsonProperty("dew_point")
    private double dewPoint;
    @JsonProperty("wind_speed")
    private double windSpeed;
    @JsonProperty("wind_deg")
    private int windDeg;
    @JsonProperty("wind_gust")
    private double windGust;
    private List<WeatherDescription> weather;
    private int clouds;
    private double pop;
    private double rain;
    private double snow;
    private double uvi;

    // 기본 생성자
    public DailyData() {}

    // 전체 필드 생성자
    public DailyData(long dt, long sunrise, long sunset, long moonrise, long moonset,
                     double moonPhase, String summary, DailyTemp temp, DailyFeelsLike feelsLike,
                     int pressure, int humidity, double dewPoint, double windSpeed, int windDeg,
                     double windGust, List<WeatherDescription> weather, int clouds,
                     double pop, double rain, double snow, double uvi) {
        this.dt = dt;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.moonrise = moonrise;
        this.moonset = moonset;
        this.moonPhase = moonPhase;
        this.summary = summary;
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.humidity = humidity;
        this.dewPoint = dewPoint;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
        this.windGust = windGust;
        this.weather = weather;
        this.clouds = clouds;
        this.pop = pop;
        this.rain = rain;
        this.snow = snow;
        this.uvi = uvi;
    }

    public long getDt() { return dt; }
    public void setDt(long dt) { this.dt = dt; }

    public long getSunrise() { return sunrise; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }

    public long getSunset() { return sunset; }
    public void setSunset(long sunset) { this.sunset = sunset; }

    public long getMoonrise() { return moonrise; }
    public void setMoonrise(long moonrise) { this.moonrise = moonrise; }

    public long getMoonset() { return moonset; }
    public void setMoonset(long moonset) { this.moonset = moonset; }

    public double getMoonPhase() { return moonPhase; }
    public void setMoonPhase(double moonPhase) { this.moonPhase = moonPhase; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public DailyTemp getTemp() { return temp; }
    public void setTemp(DailyTemp temp) { this.temp = temp; }

    public DailyFeelsLike getFeelsLike() { return feelsLike; }
    public void setFeelsLike(DailyFeelsLike feelsLike) { this.feelsLike = feelsLike; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getDewPoint() { return dewPoint; }
    public void setDewPoint(double dewPoint) { this.dewPoint = dewPoint; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public int getWindDeg() { return windDeg; }
    public void setWindDeg(int windDeg) { this.windDeg = windDeg; }

    public double getWindGust() { return windGust; }
    public void setWindGust(double windGust) { this.windGust = windGust; }

    public List<WeatherDescription> getWeather() { return weather; }
    public void setWeather(List<WeatherDescription> weather) { this.weather = weather; }

    public int getClouds() { return clouds; }
    public void setClouds(int clouds) { this.clouds = clouds; }

    public double getPop() { return pop; }
    public void setPop(double pop) { this.pop = pop; }

    public double getRain() { return rain; }
    public void setRain(double rain) { this.rain = rain; }

    public double getSnow() { return snow; }
    public void setSnow(double snow) { this.snow = snow; }

    public double getUvi() { return uvi; }
    public void setUvi(double uvi) { this.uvi = uvi; }
}
