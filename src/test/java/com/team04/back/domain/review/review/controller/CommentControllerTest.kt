package com.team04.back.domain.review.review.controller

import com.team04.back.domain.review.review.controller.CommentControllerTest.TestConfig
import com.team04.back.domain.review.review.dto.ReviewSearchDto
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.service.ReviewService
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.entity.WeatherInfo
import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.domain.weather.weather.repository.WeatherRepository
import com.team04.back.domain.weather.weather.service.WeatherService
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
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestConfig::class)
class CommentControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var reviewService: ReviewService

    @Test
    @DisplayName("커멘트 다건 조회")
    @Throws(Exception::class)
    fun t1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments")
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto(null, null, null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)
        val size = comments.getContent().size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComments"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))

        for (i in 0..size-1) {
            val comment = comments.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(comment.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(comment.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(comment.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(comment.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(comment.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(comment.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(comment.weatherInfo.date.toString())
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(comment.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 위치, 날짜 필터링")
    @Throws(Exception::class)
    fun t2_1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments")
                    .param("location", "삿포로")
                    .param("date", "2025-01-01")
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto("삿포로", LocalDate.of(2025, 1, 1), null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)
        val size = comments.getContent().size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComments"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        for (i in 0..size-1) {
            val comment = comments.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(comment.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(comment.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(comment.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(comment.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(comment.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(comment.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(comment.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(comment.weatherInfo.date.toString())
                )
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 위치, 체감 온도 필터링")
    @Throws(Exception::class)
    fun t2_2() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments")
                    .param("location", "삿포로")
                    .param("feelsLikeTemperature", "-4.0")
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto("삿포로", null, -4.0, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)
        val size = comments.getContent().size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComments"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        for (i in 0..size-1) {
            val comment = comments.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(comment.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(comment.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(comment.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(comment.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(comment.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(comment.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(comment.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(comment.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 월 필터링")
    @Throws(Exception::class)
    fun t2_3() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments")
                    .param("month", "1") // 1월 필터링
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto(null, null, null, 1, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)
        val size = comments.getContent().size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComments"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))

        for (i in 0..size-1) {
            val comment = comments.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(comment.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(comment.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(comment.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(comment.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(comment.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(comment.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(comment.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(comment.weatherInfo.date.toString())
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(comment.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 이메일 필터링")
    @Throws(Exception::class)
    fun t2_4() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments")
                    .param("email", "user1@test.com") // 이메일 필터링
            ).andDo(MockMvcResultHandlers.print())

        val search = ReviewSearchDto(null, null, null, null, "user1@test.com")
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)
        val size = comments.getContent().size

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComments"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(size))

        for (i in 0..size-1) {
            val comment = comments.content[i]
            resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].id").value(comment.id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].email").value(comment.email))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].imageUrl").value(comment.imageUrl))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].title").value(comment.title))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].sentence").value(comment.sentence))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[${i}].tagString").value(comment.tagString))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.location")
                        .value(comment.weatherInfo.location)
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.date")
                        .value(comment.weatherInfo.date.toString())
                )
                .andExpect(
                    MockMvcResultMatchers.jsonPath("$.content[${i}].weatherInfoDto.feelsLikeTemperature")
                        .value(comment.weatherInfo.feelsLikeTemperature)
                )
        }
    }

    @Test
    @DisplayName("커멘트 단건 조회")
    @Throws(Exception::class)
    fun t3() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)

        val id = comments.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments/${id}")
            ).andDo(MockMvcResultHandlers.print())

        val comment = reviewService.findById(id).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComment"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(comment.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(comment.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.imageUrl").value(comment.imageUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(comment.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$.sentence").value(comment.sentence))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tagString").value(comment.tagString))
            .andExpect(MockMvcResultMatchers.jsonPath("$.weatherInfoDto.location")
                .value(comment.weatherInfo.location))
            .andExpect(MockMvcResultMatchers.jsonPath("$.weatherInfoDto.date")
                .value(comment.weatherInfo.date.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.weatherInfoDto.feelsLikeTemperature")
                .value(comment.weatherInfo.feelsLikeTemperature))
    }

    @Test
    @DisplayName("커멘트 단건 조회 - 존재하지 않는 ID")
    @Throws(Exception::class)
    fun t3_1() {
        val id = Int.Companion.MAX_VALUE // 존재하지 않는 ID

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/comments/${id}")
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getComment"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("404-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."))
    }

    @Test
    @DisplayName("커멘트 비밀번호 검증")
    @Throws(Exception::class)
    fun t4() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)

        val id = comments.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/comments/${id}/verify-password")
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
    @DisplayName("커멘트 비밀번호 검증 - 잘못된 비밀번호")
    @Throws(Exception::class)
    fun t4_1() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)

        val id = comments.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/comments/${id}/verify-password")
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
    @DisplayName("커멘트 삭제")
    @Throws(Exception::class)
    fun t5() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)

        val id = comments.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/comments/${id}")
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("deleteComment"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${id}번 커멘트가 삭제되었습니다."))
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
    @DisplayName("커멘트 작성")
    @Throws(Exception::class)
    fun t6() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "user@test.com",
                                            "password": "1234",
                                            "title": "Test Comment",
                                            "sentence": "This is a test comment.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#comment",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        val comment = reviewService.findLatest().getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createComment"))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("201-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${comment.id}번 커멘트가 작성되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(comment.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(comment.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(comment.imageUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value(comment.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.sentence").value(comment.sentence))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.tagString").value(comment.tagString))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.location")
                    .value(comment.weatherInfo.location)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.date")
                    .value(comment.weatherInfo.date.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.feelsLikeTemperature")
                    .value(comment.weatherInfo.feelsLikeTemperature)
            )
    }

    @Test
    @DisplayName("커멘트 작성 - inValid email")
    @Throws(Exception::class)
    fun t6_1() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "invalid-email",
                                            "password": "1234",
                                            "title": "Test Comment",
                                            "sentence": "This is a test comment.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#comment",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createComment"))
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
    @DisplayName("커멘트 작성 - inValid title")
    @Throws(Exception::class)
    fun t6_2() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "user@test.com",
                                            "password": "1234",
                                            "title": "",
                                            "sentence": "This is a test comment.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#comment",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createComment"))
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
    @DisplayName("커멘트 작성 - inValid date")
    @Throws(Exception::class)
    fun t6_3() {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/comments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "email": "user@test.com",
                                            "password": "1234",
                                            "title": "Test Comment",
                                            "sentence": "This is a test comment.",
                                            "imageUrl": "http://example.com/image.jpg",
                                            "tagString": "#test#comment",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-13-01"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("createComment"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("400-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("요청 본문이 올바르지 않습니다."))
    }

    @Test
    @DisplayName("커멘트 수정")
    @Throws(Exception::class)
    fun t7() {
        val search = ReviewSearchDto(null, null, null, null, null)
        val pageable: Pageable = PageRequest.of(0, 10)
        val comments = reviewService.findBySearch(search, pageable)

        val id = comments.content[0].id

        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.put("/api/v1/comments/${id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                        {
                                            "title": "Updated Title",
                                            "sentence": "This is an updated comment.",
                                            "tagString": "#updated#comment",
                                            "imageUrl": "http://example.com/updated_image.jpg",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        
                                        """.trimIndent()
                    )
            ).andDo(MockMvcResultHandlers.print())

        val comment = reviewService.findById(id).getOrThrow()

        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(ReviewController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("modifyComment"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.resultCode").value("200-1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("${comment.id}번 커멘트가 수정되었습니다."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(comment.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value(comment.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(comment.imageUrl))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Updated Title"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.sentence").value("This is an updated comment."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.tagString").value("#updated#comment"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.location")
                    .value(comment.weatherInfo.location)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.date")
                    .value(comment.weatherInfo.date.toString())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.weatherInfoDto.feelsLikeTemperature")
                    .value(comment.weatherInfo.feelsLikeTemperature)
            )
    }
}
