package com.team04.back.domain.cloth.cloth.service;

import com.team04.back.domain.cloth.cloth.entity.ClothInfo;
import com.team04.back.domain.cloth.cloth.entity.Clothing;
import com.team04.back.domain.cloth.cloth.entity.ExtraCloth;
import com.team04.back.domain.cloth.cloth.enums.Category;
import com.team04.back.domain.cloth.cloth.repository.ClothRepository;
import com.team04.back.domain.cloth.cloth.repository.ExtraClothRepository;
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
import java.util.Set;

import static com.team04.back.common.fixture.FixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ClothServiceTest {
    @InjectMocks
    private ClothService clothService;
    @Mock
    private ClothRepository clothRepository;
    @Mock
    private ExtraClothRepository extraClothRepository;

    @Test
    @DisplayName("기간(날씨 정보 리스트)이 주어지면, 각 날씨에 적절한 의류를 추천하여 카테고리별로 반환한다.")
    void getOutfitWithPeriod() {
        WeatherInfo hotWeather = createWeatherInfo("Seoul", LocalDate.now().plusDays(1), Weather.CLEAR_SKY, 26.0);
        WeatherInfo mildWeather = createWeatherInfo("Seoul", LocalDate.now().plusDays(2), Weather.OVERCAST_CLOUDS, 16.0);
        WeatherInfo coldWeather = createWeatherInfo("Seoul", LocalDate.now().plusDays(3), Weather.HEAVY_RAIN, 4.0);
        List<WeatherInfo> weatherPlan = Arrays.asList(hotWeather, mildWeather, coldWeather);

        ClothInfo summerTee = createClothInfo(Category.CASUAL_DAILY, 25.0, 30.0);
        ClothInfo shorts = createClothInfo(Category.CASUAL_DAILY, 25.0, 30.0);
        ClothInfo springJacket = createClothInfo(Category.CASUAL_DAILY, 15.0, 24.0);
        ClothInfo jeans = createClothInfo(Category.CASUAL_DAILY, 10.0, 20.0);
        ClothInfo winterCoat = createClothInfo(Category.CASUAL_DAILY, 0.0, 10.0);
        ClothInfo scarf = createClothInfo(Category.OUTDOOR, 0.0, 10.0);
        ClothInfo formalShirt = createClothInfo(Category.FORMAL_OFFICE, 10.0, 20.0);
        ClothInfo runningShoes = createClothInfo(Category.OUTDOOR, 18.0, 28.0);

        when(clothRepository.findByTemperature(hotWeather.getFeelsLikeTemperature()))
                .thenReturn(List.of(summerTee, shorts, runningShoes));
        when(clothRepository.findByTemperature(mildWeather.getFeelsLikeTemperature()))
                .thenReturn(List.of(springJacket, jeans, formalShirt));
        when(clothRepository.findByTemperature(coldWeather.getFeelsLikeTemperature()))
                .thenReturn(List.of(winterCoat, scarf));

        ExtraCloth mask = createExtraCloth("마스크", "mask.jpg", Weather.FOG);
        ExtraCloth umbrella = createExtraCloth("우산", "umbrella.jpg", Weather.HEAVY_RAIN);

        when(extraClothRepository.findDistinctByWeather(Weather.CLEAR_SKY))
                .thenReturn(Set.of(mask));
        when(extraClothRepository.findDistinctByWeather(Weather.MODERATE_RAIN))
                .thenReturn(Set.of(umbrella));

        Map<Category, List<Clothing>> result = clothService.getOutfitWithPeriod(weatherPlan);

        assertThat(result).isNotNull();

        assertThat(result).containsKey(Category.CASUAL_DAILY);
        assertThat(result.get(Category.CASUAL_DAILY)).containsExactlyInAnyOrder(
                summerTee, shorts, springJacket, jeans, winterCoat);
        assertThat(result.get(Category.CASUAL_DAILY)).hasSize(5);

        assertThat(result).containsKey(Category.FORMAL_OFFICE);
        assertThat(result.get(Category.FORMAL_OFFICE)).containsExactlyInAnyOrder(formalShirt);
        assertThat(result.get(Category.FORMAL_OFFICE)).hasSize(1);

        assertThat(result).containsKey(Category.OUTDOOR);
        assertThat(result.get(Category.OUTDOOR)).containsExactlyInAnyOrder(runningShoes, scarf);
        assertThat(result.get(Category.OUTDOOR)).hasSize(2);

        // EXTRA 카테고리 검증
        assertThat(result).containsKey(Category.EXTRA);
        assertThat(result.get(Category.EXTRA)).containsExactlyInAnyOrder(mask, umbrella);
        assertThat(result.get(Category.EXTRA)).hasSize(2);
    }
}
