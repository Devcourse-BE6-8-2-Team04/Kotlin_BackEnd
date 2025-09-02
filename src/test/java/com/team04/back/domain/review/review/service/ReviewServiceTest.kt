package com.team04.back.domain.review.review.service

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Material
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.review.review.dto.ClothItemReqBody
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.entity.ReviewClothInfo
import com.team04.back.domain.review.review.repository.ReviewClothInfoRepository
import com.team04.back.domain.review.review.repository.ReviewRepository
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.domain.weather.weather.service.WeatherService
import com.team04.back.global.exception.ServiceException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.test.context.ActiveProfiles
import java.lang.reflect.Field
import java.time.LocalDate

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class ReviewServiceTest {

    @Mock
    private lateinit var reviewRepository: ReviewRepository

    @Mock
    private lateinit var reviewClothInfoRepository: ReviewClothInfoRepository

    @Mock
    private lateinit var clothService: ClothService

    @Mock
    private lateinit var geoService: GeoService

    @Mock
    private lateinit var weatherService: WeatherService

    @InjectMocks
    private lateinit var reviewService: ReviewService

    // 공통 테스트 데이터
    private lateinit var weatherInfo: WeatherInfo

    @BeforeEach
    fun setUp() {
        weatherInfo = WeatherInfo(Weather.CLEAR_SKY, 10.0, 20.0, 25.0, 15.0, "Seoul", LocalDate.of(2025, 1, 1))
    }

    // 테스트용 Review 실제 엔티티를 만들고, 상위 클래스까지 탐색해 id를 리플렉션으로 주입한다.
    private fun createReviewWithId(id: Int = 1): Review {
        val review = Review(null, "test@email.com", "password", "제목", "내용", "태그", "image_url", weatherInfo)
        setPrivateField(review, "id", id)
        return review
    }

    // 테스트용 ClothInfo 실제 엔티티를 만들고, 상위 클래스까지 탐색해 id를 리플렉션으로 주입한다.
    private fun createClothInfoWithId(id: Int = 10): ClothInfo {
        val clothInfo = ClothInfo.create(ClothName.T_SHIRT, "image_url", Category.TOP, null, null, null, null)
        setPrivateField(clothInfo, "id", id)
        return clothInfo
    }

    /**
     * 리플렉션으로 private/final 필드 값을 세팅한다.
     * - 상속 구조(BaseEntity 등)도 지원: 슈퍼클래스 체인을 따라 필드를 탐색.
     */
    private fun setPrivateField(target: Any, fieldName: String, value: Any) {
        var clazz: Class<*>? = target::class.java
        while (clazz != null) {
            try {
                val field: Field = clazz.getDeclaredField(fieldName)
                field.isAccessible = true
                field.set(target, value)
                return
            } catch (e: NoSuchFieldException) {
                clazz = clazz.superclass // 부모로 올라가기
            }
        }
        throw NoSuchFieldException("Field '$fieldName' not found in ${target::class.java.name}")
    }


    @Test
    @DisplayName("리뷰 삭제 시 연관된 옷 정보도 함께 삭제한다")
    fun `delete review also deletes associated cloth info`() {
        // Given
        val review = createReviewWithId(1)

        // When
        reviewService.deleteReview(review)

        // Then
        verify(reviewClothInfoRepository).deleteByReviewId(1)
        verify(reviewRepository).delete(review)
    }

    @Test
    @DisplayName("리뷰 생성 시 옷 정보가 올바르게 저장된다")
    fun `createReview saves cloth info correctly`() {
        // Given
        val clothItem = ClothItemReqBody(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, Material.COTTON, true)

        val savedReview = createReviewWithId(1)
        val defaultClothInfo = createClothInfoWithId(100)

        whenever(reviewRepository.save(any<Review>())).thenReturn(savedReview)
        whenever(clothService.findByClothNameAndStyle(ClothName.T_SHIRT, null))
            .thenReturn(defaultClothInfo)
        whenever(clothService.save(any())).thenAnswer {
            it.arguments[0] as ClothInfo
        }
        whenever(reviewClothInfoRepository.save(any<ReviewClothInfo>())).thenAnswer {
            it.arguments[0] as ReviewClothInfo
        }

        // When
        val result = reviewService.createReview(
            "test@email.com", "password", "image_url",
            "제목", "내용", "태그", weatherInfo = weatherInfo, clothList = listOf(clothItem)
        )

        // Then
        assertThat(result.id).isEqualTo(1)
        verify(clothService).findByClothNameAndStyle(ClothName.T_SHIRT, null)
        verify(clothService).save(any())
        verify(reviewClothInfoRepository).save(any())
    }

    @Test
    @DisplayName("리뷰 생성 시 옷 정보가 없으면 예외를 던진다")
    fun `createReview throws exception if cloth info not found`() {
        // Given
        val savedReview = createReviewWithId(1)
        whenever(reviewRepository.save(any<Review>())).thenReturn(savedReview)
        whenever(clothService.findByClothNameAndStyle(ClothName.T_SHIRT, null))
            .thenReturn(null)

        val clothItem = ClothItemReqBody(
            ClothName.T_SHIRT,
            Category.TOP,
            Style.CASUAL_DAILY,
            Material.COTTON,
            true
        )

        // When & Then
        assertThatThrownBy {
            reviewService.createReview(
                "test@email.com", "password", "image_url",
                "제목", "내용", "태그", weatherInfo = weatherInfo, clothList = listOf(clothItem)
            )
        }.isInstanceOf(ServiceException::class.java)
            .hasMessageContaining("옷 정보를 찾을 수 없습니다.")
    }

    @Test
    @DisplayName("리뷰 수정 시 옷 정보를 효율적으로 업데이트한다")
    fun `modify updates cloth info efficiently`() {
        // Given
        val review = createReviewWithId(1)

        // 기존 옷 정보들 (T_SHIRT만 있고, DENIM_JACKET은 새로 추가될 예정)
        val existingClothInfo1 = createClothInfoWithId(10).apply {
            // T_SHIRT 정보 설정
            setPrivateField(this, "clothName", ClothName.T_SHIRT)
            setPrivateField(this, "category", Category.TOP)
            setPrivateField(this, "style", Style.CASUAL_DAILY)
        }

        val existingReviewClothInfos = listOf(
            ReviewClothInfo(1, 10, true)   // 추천 상태가 false로 변경될 예정
        )

        // 새로운 옷 정보 (기존 T_SHIRT는 추천상태만 변경, 새로운 DENIM_JACKET 추가)
        val newClothItems = listOf(
            ClothItemReqBody(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, Material.COTTON, false), // 추천상태 변경
            ClothItemReqBody(ClothName.DENIM_JACKET, Category.TOP, Style.CASUAL_DAILY, Material.DENIM, true) // 새로 추가
        )

        val defaultClothInfoForDenimJacket = createClothInfoWithId(200)

        // Mock 설정
        whenever(reviewClothInfoRepository.findByReviewId(1)).thenReturn(existingReviewClothInfos)
        whenever(clothService.findByIdList(listOf(10))).thenReturn(listOf(existingClothInfo1))
        whenever(clothService.findByClothNameAndStyle(ClothName.DENIM_JACKET, null)).thenReturn(defaultClothInfoForDenimJacket)
        whenever(clothService.save(any())).thenAnswer { it.arguments[0] as ClothInfo }
        whenever(reviewClothInfoRepository.save(any<ReviewClothInfo>())).thenAnswer { it.arguments[0] as ReviewClothInfo }

        // When
        val modifiedReview = reviewService.modifyReview(
            review, "새 제목", "새 내용", null, null, weatherInfo = weatherInfo, clothList = newClothItems
        )

        // Then
        assertThat(modifiedReview.title).isEqualTo("새 제목")
        assertThat(modifiedReview.sentence).isEqualTo("새 내용")

        // 기존 T_SHIRT의 추천 상태가 업데이트되었는지 확인
        verify(reviewClothInfoRepository).save(argThat<ReviewClothInfo> {
            this.reviewId == 1 && this.clothInfoId == 10 && this.isRecommend == false
        })

        // 새로운 DENIM_JACKET이 생성되었는지 확인
        verify(clothService).save(argThat<ClothInfo> {
            this.clothName == ClothName.DENIM_JACKET
        })
        verify(reviewClothInfoRepository).save(argThat<ReviewClothInfo> {
            this.reviewId == 1 && this.isRecommend == true
        })
    }

    @Test
    @DisplayName("리뷰 수정 시 중복된 옷 정보는 제거된다")
    fun `modify removes duplicate cloth items`() {
        // Given
        val review = createReviewWithId(1)

        // 중복된 옷 정보 (동일한 clothName, category, style)
        val duplicateClothItems = listOf(
            ClothItemReqBody(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, Material.COTTON, true),
            ClothItemReqBody(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, Material.POLYESTER, false) // 중복
        )

        val defaultClothInfo = createClothInfoWithId(100)

        whenever(reviewClothInfoRepository.findByReviewId(1)).thenReturn(emptyList())
        whenever(clothService.findByIdList(emptyList())).thenReturn(emptyList())
        whenever(clothService.findByClothNameAndStyle(ClothName.T_SHIRT, null)).thenReturn(defaultClothInfo)
        whenever(clothService.save(any())).thenAnswer { it.arguments[0] as ClothInfo }
        whenever(reviewClothInfoRepository.save(any<ReviewClothInfo>())).thenAnswer { it.arguments[0] as ReviewClothInfo }

        // When
        reviewService.modifyReview(
            review, "새 제목", "새 내용", null, null, weatherInfo = weatherInfo, clothList = duplicateClothItems
        )

        // Then
        // ClothInfo가 한 번만 저장되어야 함 (중복 제거됨)
        verify(clothService, times(1)).save(any())
        verify(reviewClothInfoRepository, times(1)).save(any<ReviewClothInfo>())
    }

    @Test
    @DisplayName("리뷰 수정 시 기존 옷 정보의 ClothInfo가 없으면 해당 ReviewClothInfo를 삭제한다")
    fun `modify deletes ReviewClothInfo when corresponding ClothInfo not found`() {
        // Given
        val review = createReviewWithId(1)

        val existingReviewClothInfos = listOf(
            ReviewClothInfo(1, 999, true) // 존재하지 않는 clothInfoId
        )

        val newClothItems = listOf(
            ClothItemReqBody(ClothName.T_SHIRT, Category.TOP, Style.CASUAL_DAILY, Material.COTTON, true)
        )

        val defaultClothInfo = createClothInfoWithId(100)

        whenever(reviewClothInfoRepository.findByReviewId(1)).thenReturn(existingReviewClothInfos)
        whenever(clothService.findByIdList(listOf(999))).thenReturn(emptyList()) // ClothInfo가 없음
        whenever(clothService.findByClothNameAndStyle(ClothName.T_SHIRT, null)).thenReturn(defaultClothInfo)
        whenever(clothService.save(any())).thenAnswer { it.arguments[0] as ClothInfo }
        whenever(reviewClothInfoRepository.save(any<ReviewClothInfo>())).thenAnswer { it.arguments[0] as ReviewClothInfo }

        // When
        reviewService.modifyReview(
            review, "새 제목", "새 내용", null, null, weatherInfo = weatherInfo, clothList = newClothItems
        )

        // Then
        // 존재하지 않는 ClothInfo에 대한 ReviewClothInfo가 삭제되어야 함
        verify(reviewClothInfoRepository).delete(argThat<ReviewClothInfo> {
            this.reviewId == 1 && this.clothInfoId == 999
        })
    }

    @Test
    @DisplayName("리뷰 수정 시 옷 리스트가 null이면 옷 정보를 수정하지 않는다")
    fun `modify does not change cloth info if clothList is null`() {
        // Given
        val review = createReviewWithId(1)

        // When
        val modifiedReview = reviewService.modifyReview(
            review, "새 제목", "새 내용", null, null, weatherInfo = weatherInfo, clothList = null
        )

        // Then
        assertThat(modifiedReview.title).isEqualTo("새 제목")
        assertThat(modifiedReview.sentence).isEqualTo("새 내용")
        verify(reviewClothInfoRepository, never()).deleteByReviewId(any())
        verifyNoInteractions(clothService)
    }

    @Test
    @DisplayName("추천된 옷 정보만 올바르게 조회한다")
    fun `findRecommendedClothInfo retrieves recommended items correctly`() {
        // Given
        val reviewId = 1
        val reviewClothInfos = listOf(
            ReviewClothInfo(1, 10, true),
            ReviewClothInfo(1, 20, false),
            ReviewClothInfo(1, 30, true)
        )

        val cloth1 = createClothInfoWithId(10)
        val cloth2 = createClothInfoWithId(30)

        whenever(reviewClothInfoRepository.findByReviewId(reviewId)).thenReturn(reviewClothInfos)
        whenever(clothService.findByIdList(listOf(10, 30))).thenReturn(listOf(cloth1, cloth2))

        // When
        val result = reviewService.findRecommendedClothInfo(reviewId)

        // Then
        assertThat(result.map { it.id }).containsExactly(10, 30)
    }

    @Test
    @DisplayName("비추천된 옷 정보만 올바르게 조회한다")
    fun `findNonRecommendedClothInfo retrieves non-recommended items correctly`() {
        // Given
        val reviewId = 1
        val reviewClothInfos = listOf(
            ReviewClothInfo(1, 10, true),
            ReviewClothInfo(1, 20, false),
            ReviewClothInfo(1, 30, true)
        )

        val cloth = createClothInfoWithId(20)

        whenever(reviewClothInfoRepository.findByReviewId(reviewId)).thenReturn(reviewClothInfos)
        whenever(clothService.findByIdList(listOf(20))).thenReturn(listOf(cloth))

        // When
        val result = reviewService.findNonRecommendedClothInfo(reviewId)

        // Then
        assertThat(result.map { it.id }).containsExactly(20)
    }
}
