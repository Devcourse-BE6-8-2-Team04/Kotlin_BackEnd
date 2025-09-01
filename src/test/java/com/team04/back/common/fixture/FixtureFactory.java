package com.team04.back.common.fixture;

import com.team04.back.domain.cloth.cloth.entity.ClothInfo;
import com.team04.back.domain.cloth.cloth.enums.Category;
import com.team04.back.domain.cloth.cloth.enums.ClothName;
import com.team04.back.domain.cloth.cloth.enums.Style;
import com.team04.back.domain.history.history.entity.ClothRecommendationHistory;
import com.team04.back.domain.user.user.entity.User;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FixtureFactory {

    public static ClothInfo createClothInfo(Style style, double minTemp, double maxTemp) {
        return ClothInfo.create(
                ClothName.T_SHIRT,
                "test_image.jpg",
                Category.TOP,
                style,
                null,
                minTemp,
                maxTemp
        );
    }


    public static WeatherInfo createWeatherInfo(String location, LocalDate date, Weather weather, Double feelsLikeTemperature) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setLocation(location);
        weatherInfo.setDate(date);
        weatherInfo.setWeather(weather);
        weatherInfo.setFeelsLikeTemperature(feelsLikeTemperature);

        LocalDateTime now = LocalDateTime.now();
        weatherInfo.setCreateDate(now);
        weatherInfo.setModifyDate(now);

        return weatherInfo;
    }

    public static WeatherInfo createDefaultWeatherInfo(String location, LocalDate date) {
        return createWeatherInfo(location, date, Weather.CLEAR_SKY, 20.0);
    }

    public static List<WeatherInfo> createWeatherInfoList(String location, int futureDays) {
        LocalDate today = LocalDate.now();

        return IntStream.range(0, futureDays)
                .mapToObj(i -> {
                    LocalDate date = today.plusDays(i);
                    Weather weather = Weather.values()[i % Weather.values().length];
                    double minTemp = 15.0 + i * 0.1;
                    double maxTemp = 25.0 + i * 0.3;
                    return createWeatherInfo(location, date, weather, (minTemp + maxTemp) / 2);
                })
                .collect(Collectors.toList());
    }

    public static ClothRecommendationHistory createClothRecommendationHistory(
            User user,
            String location,
            List<WeatherInfo> weatherInfos,
            List<ClothInfo> likedClothings,
            List<ClothInfo> unLikedClothings
    ) {
        return new ClothRecommendationHistory(
                user,
                location,
                weatherInfos,
                likedClothings,
                unLikedClothings,
                20.0,   // feelsLike
                5.0,    // uvi
                0.1,    // rain
                0.0,    // snow
                60,     // humidity
                3.5,    // windSpeed
                18.0,   // tempMin
                27.0,   // tempMax
                9.0,    // dailyTemperatureGap
                LocalDateTime.now()
        );
    }

    public static ClothRecommendationHistory createClothRecommendationHistory(
            int id,
            User user,
            String location,
            List<WeatherInfo> weatherInfos,
            List<ClothInfo> likedClothings,
            List<ClothInfo> unLikedClothings
    ) {
        ClothRecommendationHistory history = createClothRecommendationHistory(
                user,
                location,
                weatherInfos,
                likedClothings,
                unLikedClothings
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