package com.team04.back.domain.cloth.extracloth.entity;

import com.team04.back.domain.cloth.cloth.entity.ExtraCloth;
import com.team04.back.domain.weather.weather.enums.Weather;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExtraClothTest {

    private static final String VALID_CLOTH_NAME = "Test Extra Cloth";
    private static final String VALID_IMAGE_URL = "http://example.com/extra_image.jpg";
    private static final Weather VALID_WEATHER = Weather.CLEAR_SKY;

    @Test
    @DisplayName("ExtraCloth 생성 테스트")
    void extraClothCreationTest() {
        ExtraCloth extraCloth = ExtraCloth.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_WEATHER
        );

        assertThat(extraCloth).isNotNull();
        assertThat(extraCloth.getClothName()).isEqualTo(VALID_CLOTH_NAME);
        assertThat(extraCloth.getImageUrl()).isEqualTo(VALID_IMAGE_URL);
        assertThat(extraCloth.getWeather()).isEqualTo(VALID_WEATHER);
    }

    @Nested
    @DisplayName("ExtraCloth 생성 시 인자 체크")
    class ExtraClothCreationArgumentTests {
        @Test
        @DisplayName("clothName이 null일 경우 IllegalArgumentException 발생")
        void whenClothNameIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ExtraCloth.create(null, VALID_IMAGE_URL, VALID_WEATHER)
            );
        }

        @Test
        @DisplayName("clothName이 빈 문자열일 경우 IllegalArgumentException 발생")
        void whenClothNameIsBlank() {
            assertThrows(IllegalArgumentException.class, () ->
                    ExtraCloth.create("", VALID_IMAGE_URL, VALID_WEATHER)
            );
        }

        @Test
        @DisplayName("imageUrl이 null일 경우 IllegalArgumentException 발생")
        void whenImageUrlIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ExtraCloth.create(VALID_CLOTH_NAME, null, VALID_WEATHER)
            );
        }

        @Test
        @DisplayName("imageUrl이 빈 문자열일 경우 IllegalArgumentException 발생")
        void whenImageUrlIsBlank() {
            assertThrows(IllegalArgumentException.class, () ->
                    ExtraCloth.create(VALID_CLOTH_NAME, "", VALID_WEATHER)
            );
        }

        @Test
        @DisplayName("weather가 null일 경우 IllegalArgumentException 발생")
        void whenWeatherIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ExtraCloth.create(VALID_CLOTH_NAME, VALID_IMAGE_URL, null)
            );
        }
    }

    @Test
    @DisplayName("모든 필드를 유효한 값으로 업데이트")
    void updateExtraCloth_allFields_success() {
        ExtraCloth extraCloth = ExtraCloth.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_WEATHER
        );

        String newClothName = "Updated Extra Cloth";
        String newImageUrl = "http://example.com/new_extra_image.png";
        Weather newWeather = Weather.LIGHT_RAIN;

        extraCloth.update(newClothName, newImageUrl, newWeather);

        assertThat(extraCloth.getClothName()).isEqualTo(newClothName);
        assertThat(extraCloth.getImageUrl()).isEqualTo(newImageUrl);
        assertThat(extraCloth.getWeather()).isEqualTo(newWeather);
    }

    @Test
    @DisplayName("일부 필드만 이용해 업데이트")
    void updateExtraCloth_partialUpdate_clothName() {
        ExtraCloth extraCloth = ExtraCloth.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_WEATHER
        );

        String newClothName = "Only Name Updated";

        extraCloth.update(newClothName, null, null);

        assertThat(extraCloth.getClothName()).isEqualTo(newClothName);
        assertThat(extraCloth.getImageUrl()).isEqualTo(VALID_IMAGE_URL);  
        assertThat(extraCloth.getWeather()).isEqualTo(VALID_WEATHER);  
    }

    @Test
    @DisplayName("Null 또는 빈 문자열 인자는 해당 필드를 업데이트하지 않음")
    void updateExtraCloth_nullOrBlankArgs_noChange() {
        ExtraCloth extraCloth = ExtraCloth.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_WEATHER
        );

        extraCloth.update("", "", null);

        assertThat(extraCloth.getClothName()).isEqualTo(VALID_CLOTH_NAME);
        assertThat(extraCloth.getImageUrl()).isEqualTo(VALID_IMAGE_URL);  
        assertThat(extraCloth.getWeather()).isEqualTo(VALID_WEATHER);  
    }
}