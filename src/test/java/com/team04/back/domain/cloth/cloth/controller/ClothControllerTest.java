package com.team04.back.domain.cloth.cloth.controller;

import com.team04.back.domain.cloth.cloth.dto.CategoryClothDto;
import com.team04.back.domain.cloth.cloth.dto.WeatherClothResponseDto;
import com.team04.back.domain.cloth.cloth.enums.Category;
import com.team04.back.domain.cloth.cloth.enums.ClothName;
import com.team04.back.domain.cloth.cloth.enums.Style;
import com.team04.back.domain.cloth.cloth.service.ClothService;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;
import com.team04.back.domain.weather.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class ClothControllerTest {

    private static final double LATITUDE = 37.5;
    private static final double LONGITUDE = 127.0;
    private static final String LOCATION = "서울";

    private static final double NORMAL_TEMP = 23.0;
    private static final double HEATWAVE_TEMP = 33.0;

    private static final CategoryClothDto CLOTH_TOP = new CategoryClothDto(
            ClothName.T_SHIRT, "/images/tshirt.png", Category.TOP, Style.CASUAL_DAILY, null
    );
    private static final CategoryClothDto CLOTH_BOTTOM = new CategoryClothDto(
            ClothName.JEANS, "/images/jeans.png", Category.BOTTOM, Style.CASUAL_DAILY, null
    );

    private ClothController clothController;

    @Mock
    private WeatherService weatherService;

    @Mock
    private ClothService clothService;

    private WeatherInfo createWeatherInfo(Weather weather, double feelsLike, double min, double max) {
        WeatherInfo info = new WeatherInfo();
        info.setWeather(weather);
        info.setFeelsLikeTemperature(feelsLike);
        info.setMinTemperature(min);
        info.setMaxTemperature(max);
        info.setLocation(LOCATION);
        info.setDate(LocalDate.now());
        return info;
    }

    @BeforeEach
    void setup() {
        openMocks(this);
        clothController = new ClothController(clothService, weatherService);
    }

    @Test
    void getClothDetails_ReturnsWeatherAndClothes() {
        // given
        WeatherInfo normalWeather = createWeatherInfo(Weather.CLEAR_SKY, NORMAL_TEMP, 18, 28);
        List<CategoryClothDto> normalCloths = List.of(CLOTH_TOP, CLOTH_BOTTOM);

        when(weatherService.getWeatherInfo(anyDouble(), anyDouble(), any()))
                .thenReturn(normalWeather);
        when(clothService.findClothByWeather(NORMAL_TEMP))
                .thenReturn(normalCloths);

        // when
        WeatherClothResponseDto response = clothController.getClothDetails(LATITUDE, LONGITUDE);

        // then
        assertThat(response.getWeatherInfo().getWeather()).isEqualTo(Weather.CLEAR_SKY.name());
        assertThat(response.getClothList()).hasSize(2);
        assertThat(response.getClothList()).extracting("clothName")
                .containsExactly(ClothName.T_SHIRT, ClothName.JEANS);
    }

    @Test
    void getClothDetails_WithHeatWave_ReturnsCorrectClothes() {
        // given
        WeatherInfo heatwaveWeather = createWeatherInfo(Weather.HEAT_WAVE, HEATWAVE_TEMP, 28, 36);
        List<CategoryClothDto> heatwaveCloths = List.of(CLOTH_TOP, CLOTH_BOTTOM);

        when(weatherService.getWeatherInfo(anyDouble(), anyDouble(), any()))
                .thenReturn(heatwaveWeather);
        when(clothService.findClothByWeather(HEATWAVE_TEMP))
                .thenReturn(heatwaveCloths);

        // when
        WeatherClothResponseDto response = clothController.getClothDetails(LATITUDE, LONGITUDE);

        // then
        assertThat(response.getWeatherInfo().getWeather()).isEqualTo(Weather.HEAT_WAVE.name());
        assertThat(response.getClothList()).hasSize(2);
    }
}