package com.team04.back.domain.cloth.cloth.service;

import com.team04.back.domain.cloth.cloth.entity.ClothInfo;
import com.team04.back.domain.cloth.cloth.enums.Style;
import com.team04.back.domain.cloth.cloth.repository.ClothRepository;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.team04.back.common.fixture.FixtureFactory.createClothInfo;
import static com.team04.back.common.fixture.FixtureFactory.createWeatherInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ClothServiceTest {
    @InjectMocks
    private ClothService clothService;
    @Mock
    private ClothRepository clothRepository;

    @Test
    @DisplayName("기간(날씨 정보 리스트)이 주어지면, 각 날씨에 적절한 의류를 추천하여 카테고리별로 반환한다.")
    void getOutfitWithPeriod() {
        WeatherInfo hotWeather = createWeatherInfo("Seoul", LocalDate.now().plusDays(1), Weather.CLEAR_SKY, 26.0);
        WeatherInfo mildWeather = createWeatherInfo("Seoul", LocalDate.now().plusDays(2), Weather.OVERCAST_CLOUDS, 16.0);
        WeatherInfo coldWeather = createWeatherInfo("Seoul", LocalDate.now().plusDays(3), Weather.HEAVY_RAIN, 4.0);
        List<WeatherInfo> weatherPlan = Arrays.asList(hotWeather, mildWeather, coldWeather);

        ClothInfo summerTee = createClothInfo(Style.CASUAL_DAILY, 25.0, 30.0);
        ClothInfo shorts = createClothInfo(Style.CASUAL_DAILY, 25.0, 30.0);
        ClothInfo springJacket = createClothInfo(Style.CASUAL_DAILY, 15.0, 24.0);
        ClothInfo jeans = createClothInfo(Style.CASUAL_DAILY, 10.0, 20.0);
        ClothInfo winterCoat = createClothInfo(Style.CASUAL_DAILY, 0.0, 10.0);
        ClothInfo scarf = createClothInfo(Style.OUTDOOR, 0.0, 10.0);
        ClothInfo formalShirt = createClothInfo(Style.FORMAL_OFFICE, 10.0, 20.0);
        ClothInfo runningShoes = createClothInfo(Style.OUTDOOR, 18.0, 28.0);

        when(clothRepository.findByTemperature(hotWeather.getFeelsLikeTemperature()))
                .thenReturn(List.of(summerTee, shorts, runningShoes));
        when(clothRepository.findByTemperature(mildWeather.getFeelsLikeTemperature()))
                .thenReturn(List.of(springJacket, jeans, formalShirt));
        when(clothRepository.findByTemperature(coldWeather.getFeelsLikeTemperature()))
                .thenReturn(List.of(winterCoat, scarf));

        // ✅ ClothInfo 기반으로 수정
        Map<Style, List<ClothInfo>> result = clothService.getOutfitWithPeriod(weatherPlan);

        assertThat(result).isNotNull();

        assertThat(result).containsKey(Style.CASUAL_DAILY);
        assertThat(result.get(Style.CASUAL_DAILY)).containsExactlyInAnyOrder(
                summerTee, shorts, springJacket, jeans, winterCoat);
        assertThat(result.get(Style.CASUAL_DAILY)).hasSize(5);

        assertThat(result).containsKey(Style.FORMAL_OFFICE);
        assertThat(result.get(Style.FORMAL_OFFICE)).containsExactlyInAnyOrder(formalShirt);
        assertThat(result.get(Style.FORMAL_OFFICE)).hasSize(1);

        assertThat(result).containsKey(Style.OUTDOOR);
        assertThat(result.get(Style.OUTDOOR)).containsExactlyInAnyOrder(runningShoes, scarf);
        assertThat(result.get(Style.OUTDOOR)).hasSize(2);
    }
}
