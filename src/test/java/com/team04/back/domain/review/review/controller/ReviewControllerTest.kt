package com.team04.back.domain.review.review.controller

import com.team04.back.domain.review.review.controller.ReviewControllerTest.TestConfig
import com.team04.back.standard.dto.ReviewSearchDto
import com.team04.back.domain.review.review.service.ReviewService
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.domain.weather.weather.repository.WeatherRepository
import com.team04.back.domain.weather.weather.service.WeatherService
import com.team04.back.global.initData.TestInitData
import com.team04.back.standard.dto.ReviewSearchSortType
import com.team04.back.standard.dto.ReviewSearchSortType.ID
import com.team04.back.standard.extensions.getOrThrow
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestConfig::class, TestInitData::class)
class ReviewControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var reviewService: ReviewService

    @Test
    @DisplayName("리뷰 다건 조회")
    @Throws(Exception::class)
    fun t1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews")
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto(null, null, null, null, null)
        val reviews = reviewService.findBySearch(search)
        val size = reviews.content.size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReviews"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))

        for (i in 0..size - 1) {
            val review = reviews.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(review.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(review.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(review.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(review.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(review.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(review.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(review.weatherInfo.date.toString())
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(review.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("리뷰 조건 조회 - 위치, 날짜 필터링")
    @Throws(Exception::class)
    fun t2_1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews")
                    .param("location", "삿포로")
                    .param("date", "2025-01-01")
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto("삿포로", LocalDate.of(2025, 1, 1), null, null, null)
        val reviews = reviewService.findBySearch(search)
        val size = reviews.content.size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReviews"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        for (i in 0..size - 1) {
            val review = reviews.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(review.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(review.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(review.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(review.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(review.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(review.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(review.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(review.weatherInfo.date.toString())
                )
        }
    }

    @Test
    @DisplayName("리뷰 조건 조회 - 위치, 체감 온도 필터링")
    @Throws(Exception::class)
    fun t2_2() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews")
                    .param("location", "삿포로")
                    .param("feelsLikeTemperature", "-4.0")
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto("삿포로", null, -4.0, null, null)
        val reviews = reviewService.findBySearch(search)
        val size = reviews.content.size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReviews"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        for (i in 0..size - 1) {
            val review = reviews.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(review.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(review.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(review.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(review.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(review.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(review.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(review.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(review.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("리뷰 조건 조회 - 월 필터링")
    @Throws(Exception::class)
    fun t2_3() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews")
                    .param("month", "1") // 1월 필터링
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto(null, null, null, 1, null)
        val reviews = reviewService.findBySearch(search)
        val size = reviews.content.size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReviews"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))

        for (i in 0..size - 1) {
            val review = reviews.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(review.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(review.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(review.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(review.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(review.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(review.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(review.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(review.weatherInfo.date.toString())
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(review.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("리뷰 조건 조회 - 이메일 필터링")
    @Throws(Exception::class)
    fun t2_4() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews")
                    .param("email", "user1@test.com") // 이메일 필터링
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto(null, null, null, null, "user1@test.com")
        val reviews = reviewService.findBySearch(search)
        val size = reviews.content.size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReviews"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))

        for (i in 0..size - 1) {
            val review = reviews.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(review.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(review.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(review.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(review.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(review.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(review.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(review.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(review.weatherInfo.date.toString())
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(review.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("리뷰 단건 조회")
    @Throws(Exception::class)
    fun t3() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val reviews = reviewService.findBySearch(search)

        val id = reviews.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews/${id}")
            ).andDo(MockMvcResultHandlers.print())

        val review = reviewService.findById(id).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReview"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(review.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(review.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value(review.imageUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(review.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$.sentence").value(review.sentence))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tagString").value(review.tagString))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.weatherInfoDto.location")
                    .value(review.weatherInfo.location)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.weatherInfoDto.date")
                    .value(review.weatherInfo.date.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.weatherInfoDto.feelsLikeTemperature")
                    .value(review.weatherInfo.feelsLikeTemperature)
            )
    }

    @Test
    @DisplayName("리뷰 단건 조회 - 존재하지 않는 ID")
    @Throws(Exception::class)
    fun t3_1() {
        val id = Int.Companion.MAX_VALUE // 존재하지 않는 ID

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/reviews/${id}")
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getReview"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("404-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."))
    }

    @Test
    @DisplayName("리뷰 비밀번호 검증")
    @Throws(Exception::class)
    fun t4() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val reviews = reviewService.findBySearch(search)

        val id = reviews.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/reviews/${id}/verify-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "password": "1234"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("verifyPassword"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("비밀번호가 일치합니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
    }

    @Test
    @DisplayName("리뷰 비밀번호 검증 - 잘못된 비밀번호")
    @Throws(Exception::class)
    fun t4_1() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val reviews = reviewService.findBySearch(search)

        val id = reviews.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/reviews/${id}/verify-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "password": "wrong-password"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("verifyPassword"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("비밀번호가 일치하지 않습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(false))
    }

    @Test
    @DisplayName("리뷰 삭제")
    @Throws(Exception::class)
    fun t5() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val reviews = reviewService.findBySearch(search)

        val id = reviews.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/reviews/${id}")
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("deleteReview"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${id}번 리뷰가 삭제되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(id))
    }


    @Autowired
    private lateinit var weatherService: WeatherService

    @Autowired
    private lateinit var weatherRepository: WeatherRepository

    @Autowired
    private lateinit var geoService: GeoService

    @TestConfiguration
    internal class TestConfig {
        @Bean
        fun weatherService(): WeatherService = mockk()

        @Bean
        fun geoService(): GeoService = mockk()
    }

    @BeforeEach
    fun setUp() {
        val mockWeatherInfo = WeatherInfo(
            Weather.CLEAR_SKY,
            10.0,
            20.0,
            25.0,
            15.0,
            "Seoul",
            LocalDate.of(2025, 1, 1)
        )

        val saved = weatherRepository.save(mockWeatherInfo)

        every { geoService.getCoordinatesFromLocation("Seoul", "KR") } returns listOf(37.5665, 126.9780)
        every { weatherService.getWeatherInfo("Seoul", any(), any(), any()) } returns saved
    }


    @Test
    @DisplayName("리뷰 작성")
    @Throws(Exception::class)
    fun t6() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "user@test.com",
                                            "password": "1234",
                                            "title": "Test Review",
                                            "sentence": "This is a test review.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#review",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        val review = reviewService.findLatest().getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createReview"))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${review.id}번 리뷰가 작성되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(review.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(review.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(review.imageUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value(review.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.sentence").value(review.sentence))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.tagString").value(review.tagString))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.location")
                    .value(review.weatherInfo.location)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.date")
                    .value(review.weatherInfo.date.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.feelsLikeTemperature")
                    .value(review.weatherInfo.feelsLikeTemperature)
            )
    }

    @Test
    @DisplayName("리뷰 작성 - inValid email")
    @Throws(Exception::class)
    fun t6_1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "invalid-email",
                                            "password": "1234",
                                            "title": "Test Review",
                                            "sentence": "This is a test review.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#review",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createReview"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value(
                    """
                        email-Email-must be a well-formed email address
                        """.trimIndent()
                )
            )
    }

    @Test
    @DisplayName("리뷰 작성 - inValid title")
    @Throws(Exception::class)
    fun t6_2() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "user@test.com",
                                            "password": "1234",
                                            "title": "",
                                            "sentence": "This is a test review.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#review",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createReview"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.msg").value(
                    """
                        title-NotBlank-must not be blank
                        title-Size-size must be between 2 and 100
                        """.trimIndent()
                )
            )
    }

    @Test
    @DisplayName("리뷰 작성 - inValid date")
    @Throws(Exception::class)
    fun t6_3() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/reviews")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "user@test.com",
                                            "password": "1234",
                                            "title": "Test Review",
                                            "sentence": "This is a test review.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#review",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-13-01"
                                        }
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createReview"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("요청 본문이 올바르지 않습니다."))
    }

    @Test
    @DisplayName("리뷰 수정")
    @Throws(Exception::class)
    fun t7() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val reviews = reviewService.findBySearch(search)

        val id = reviews.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.put("/api/v1/reviews/${id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "Updated Title",
                                            "sentence": "This is an updated review.",
                                            "tagString": "#updated#review",
                                            "imageUrl": "http://example.com/updated_image.jpg",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        val review = reviewService.findById(id).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modifyReview"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${review.id}번 리뷰가 수정되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(review.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(review.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(review.imageUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Updated Title"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.sentence").value("This is an updated review."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.tagString").value("#updated#review"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.location")
                    .value(review.weatherInfo.location)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.date")
                    .value(review.weatherInfo.date.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.feelsLikeTemperature")
                    .value(review.weatherInfo.feelsLikeTemperature)
            )
    }
}
