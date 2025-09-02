package com.team04.back.common.fixture;

import com.team04.back.domain.cloth.cloth.entity.ClothInfo;
import com.team04.back.domain.cloth.cloth.enums.Category;
import com.team04.back.domain.cloth.cloth.enums.ClothName;
import com.team04.back.domain.cloth.cloth.enums.Style;
import com.team04.back.domain.history.history.entity.ClothRecommendationHistory;
import com.team04.back.domain.member.member.entity.Member;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FixtureFactory {

    public static ClothInfo createClothInfo(ClothName clothName, Category category, Style style, double minTemp, double maxTemp) {
        return ClothInfo.create(
                clothName,
                "test_image.jpg",
                category,
                style,
                null,
                minTemp,
                maxTemp
        );
    }

    public static ClothInfo createClothInfo(Style style, double minTemp, double maxTemp) {
        return createClothInfo(ClothName.T_SHIRT, Category.TOP, style, minTemp, maxTemp);
    }


    public static WeatherInfo createWeatherInfo(String location, LocalDate date, Weather weather, Double feelsLikeTemperature, Double dailyTemperatureGap, Double rain, Double snow, Double windSpeed, Double uvi) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setLocation(location);
        weatherInfo.setDate(date);
        weatherInfo.setWeather(weather);
        weatherInfo.setFeelsLikeTemperature(feelsLikeTemperature);
        weatherInfo.setDailyTemperatureGap(dailyTemperatureGap);
        weatherInfo.setRain(rain);
        weatherInfo.setSnow(snow);
        weatherInfo.setWindSpeed(windSpeed);
        weatherInfo.setUvi(uvi);

        LocalDateTime now = LocalDateTime.now();
        weatherInfo.setCreateDate(now);
        weatherInfo.setModifyDate(now);

        return weatherInfo;
    }

    public static WeatherInfo createDefaultWeatherInfo(String location, LocalDate date) {
        return createWeatherInfo(location, date, Weather.CLEAR_SKY, 20.0, 10.0, 0.0, 0.0, 5.0, 3.0);
    }

    public static List<WeatherInfo> createWeatherInfoList(String location, int futureDays) {
        LocalDate today = LocalDate.now();

        return IntStream.range(0, futureDays)
                .mapToObj(i -> {
                    LocalDate date = today.plusDays(i);
                    Weather weather = Weather.values()[i % Weather.values().length];
                    double feelsLikeTemperature = 15.0 + i * 0.5;
                    double dailyTemperatureGap = 8.0 + i * 0.1;
                    double rain = (i % 3 == 0) ? 5.0 : 0.0;
                    double snow = (i % 5 == 0) ? 2.0 : 0.0;
                    double windSpeed = 3.0 + i * 0.2;
                    double uvi = 4.0 + i * 0.1;
                    return createWeatherInfo(location, date, weather, feelsLikeTemperature, dailyTemperatureGap, rain, snow, windSpeed, uvi);
                })
                .collect(Collectors.toList());
    }

    public static ClothRecommendationHistory createClothRecommendationHistory(
            Member member,
            String location,
            List<WeatherInfo> weatherInfos,
            List<ClothInfo> likedClothings,
            List<ClothInfo> dislikedClothings
    ) {
        LocalDate date = weatherInfos.isEmpty() ? LocalDate.now() : weatherInfos.get(0).getDate();
        WeatherInfo mainWeatherInfo = weatherInfos.isEmpty() ? createDefaultWeatherInfo(location, date) : weatherInfos.get(0);

        return new ClothRecommendationHistory(
                member,
                location,
                date,
                weatherInfos,
                likedClothings,
                dislikedClothings,
                mainWeatherInfo.getFeelsLikeTemperature(),
                mainWeatherInfo.getUvi() != null ? mainWeatherInfo.getUvi() : 0.0,
                mainWeatherInfo.getRain() != null ? mainWeatherInfo.getRain() : 0.0,
                mainWeatherInfo.getSnow() != null ? mainWeatherInfo.getSnow() : 0.0,
                mainWeatherInfo.getHumidity() != null ? mainWeatherInfo.getHumidity() : 0,
                mainWeatherInfo.getWindSpeed() != null ? mainWeatherInfo.getWindSpeed() : 0.0,
                mainWeatherInfo.getMinTemperature(),
                mainWeatherInfo.getMaxTemperature(),
                mainWeatherInfo.getDailyTemperatureGap(),
                LocalDateTime.now()
        );
    }

    public static ClothRecommendationHistory createClothRecommendationHistory(
            int id,
            Member member,
            String location,
            List<WeatherInfo> weatherInfos,
            List<ClothInfo> likedClothings,
            List<ClothInfo> dislikedClothings
    ) {
        ClothRecommendationHistory history = createClothRecommendationHistory(
                member,
                location,
                weatherInfos,
                likedClothings,
                dislikedClothings
        );
        try {
            Field idField = history.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(history, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return history;
    }
}