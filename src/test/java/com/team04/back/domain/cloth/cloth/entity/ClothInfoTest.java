package com.team04.back.domain.cloth.cloth.entity;

import com.team04.back.domain.cloth.cloth.enums.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClothInfoTest {

    private static final String VALID_CLOTH_NAME = "Test Cloth";
    private static final String VALID_IMAGE_URL = "http://example.com/image.jpg";
    private static final Category VALID_CATEGORY = Category.CASUAL_DAILY;
    private static final Double VALID_MIN_FEELS_LIKE = 15.0;
    private static final Double VALID_MAX_FEELS_LIKE = 25.0;

    @Test
    @DisplayName("ClothInfo 생성 테스트")
    void clothCreationTest() {
        ClothInfo clothInfo = ClothInfo.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_CATEGORY,
                VALID_MIN_FEELS_LIKE,
                VALID_MAX_FEELS_LIKE
        );

        assertThat(clothInfo).isNotNull();
        assertThat(clothInfo.getClothName()).isEqualTo(VALID_CLOTH_NAME);
        assertThat(clothInfo.getImageUrl()).isEqualTo(VALID_IMAGE_URL);
        assertThat(clothInfo.getCategory()).isEqualTo(VALID_CATEGORY);
        assertThat(clothInfo.getMinFeelsLike()).isEqualTo(VALID_MIN_FEELS_LIKE);
        assertThat(clothInfo.getMaxFeelsLike()).isEqualTo(VALID_MAX_FEELS_LIKE);
    }

    @Nested
    @DisplayName("ClothInfo 생성 시 인자 체크")
    class ClothCreationArgumentTests {
        @Test
        @DisplayName("clothName이 null일 경우 IllegalArgumentException 발생")
        void whenClothNameIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create(null, VALID_IMAGE_URL, VALID_CATEGORY, VALID_MIN_FEELS_LIKE, VALID_MAX_FEELS_LIKE)
            );
        }

        @Test
        @DisplayName("clothName이 빈 문자열일 경우 IllegalArgumentException 발생")
        void whenClothNameIsBlank() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create("", VALID_IMAGE_URL, VALID_CATEGORY, VALID_MIN_FEELS_LIKE, VALID_MAX_FEELS_LIKE)
            );
        }

        @Test
        @DisplayName("imageUrl이 null일 경우 IllegalArgumentException 발생")
        void whenImageUrlIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create(VALID_CLOTH_NAME, null, VALID_CATEGORY, VALID_MIN_FEELS_LIKE, VALID_MAX_FEELS_LIKE)
            );
        }

        @Test
        @DisplayName("imageUrl이 빈 문자열일 경우 IllegalArgumentException 발생")
        void whenImageUrlIsBlank() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create(VALID_CLOTH_NAME, "", VALID_CATEGORY, VALID_MIN_FEELS_LIKE, VALID_MAX_FEELS_LIKE)
            );
        }

        @Test
        @DisplayName("category가 null일 경우 IllegalArgumentException 발생")
        void whenCategoryIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create(VALID_CLOTH_NAME, VALID_IMAGE_URL, null, VALID_MIN_FEELS_LIKE, VALID_MAX_FEELS_LIKE)
            );
        }

        @Test
        @DisplayName("maxFeelsLike이 null일 경우 IllegalArgumentException 발생")
        void whenMaxFeelsLikeIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create(VALID_CLOTH_NAME, VALID_IMAGE_URL, VALID_CATEGORY, VALID_MAX_FEELS_LIKE, null)
            );
        }

        @Test
        @DisplayName("minFeelsLike이 null일 경우 IllegalArgumentException 발생")
        void whenMinFeelsLikeIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    ClothInfo.create(VALID_CLOTH_NAME, VALID_IMAGE_URL, VALID_CATEGORY, null, VALID_MIN_FEELS_LIKE)
            );
        }
    }

    @Test
    @DisplayName("모든 필드를 유효한 값으로 업데이트")
    void updateClothInfo_allFields_success() {
        ClothInfo clothInfo = ClothInfo.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_CATEGORY,
                VALID_MIN_FEELS_LIKE,
                VALID_MAX_FEELS_LIKE
        );

        String newClothName = "Updated Cloth";
        String newImageUrl = "http://example.com/new_image.png";
        Category newCategory = Category.FORMAL_OFFICE;
        Double newMaxFeelsLike = 30.0;
        Double newMinFeelsLike = 20.0;

        clothInfo.update(newClothName, newImageUrl, newCategory, newMaxFeelsLike, newMinFeelsLike);

        assertThat(clothInfo.getClothName()).isEqualTo(newClothName);
        assertThat(clothInfo.getImageUrl()).isEqualTo(newImageUrl);
        assertThat(clothInfo.getCategory()).isEqualTo(newCategory);
        assertThat(clothInfo.getMinFeelsLike()).isEqualTo(newMinFeelsLike);
        assertThat(clothInfo.getMaxFeelsLike()).isEqualTo(newMaxFeelsLike);
    }

    @Test
    @DisplayName("일부 필드만 이용해 업데이트")
    void updateClothInfo_partialUpdate_clothName() {
        ClothInfo clothInfo = ClothInfo.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_CATEGORY,
                VALID_MIN_FEELS_LIKE,
                VALID_MAX_FEELS_LIKE
        );

        String newClothName = "Only Name Updated";

        clothInfo.update(newClothName, null, null, null, null);

        assertThat(clothInfo.getClothName()).isEqualTo(newClothName);
        assertThat(clothInfo.getImageUrl()).isEqualTo(VALID_IMAGE_URL);
        assertThat(clothInfo.getCategory()).isEqualTo(VALID_CATEGORY);
        assertThat(clothInfo.getMinFeelsLike()).isEqualTo(VALID_MIN_FEELS_LIKE);
        assertThat(clothInfo.getMaxFeelsLike()).isEqualTo(VALID_MAX_FEELS_LIKE);
    }

    @Test
    @DisplayName("Null 또는 빈 문자열 인자는 해당 필드를 업데이트하지 않음")
    void updateClothInfo_nullOrBlankArgs_noChange() {
        ClothInfo clothInfo = ClothInfo.create(
                VALID_CLOTH_NAME,
                VALID_IMAGE_URL,
                VALID_CATEGORY,
                VALID_MIN_FEELS_LIKE,
                VALID_MAX_FEELS_LIKE
        );

        clothInfo.update("", "", null, null, null);

        assertThat(clothInfo.getClothName()).isEqualTo(VALID_CLOTH_NAME);
        assertThat(clothInfo.getImageUrl()).isEqualTo(VALID_IMAGE_URL);
        assertThat(clothInfo.getCategory()).isEqualTo(VALID_CATEGORY);
        assertThat(clothInfo.getMinFeelsLike()).isEqualTo(VALID_MIN_FEELS_LIKE);
        assertThat(clothInfo.getMaxFeelsLike()).isEqualTo(VALID_MAX_FEELS_LIKE);
    }
}