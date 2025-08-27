package com.team04.back.domain.review.review.controller;

import com.team04.back.domain.review.review.entity.Review;
import com.team04.back.domain.review.review.service.ReviewService;
import com.team04.back.domain.review.review.dto.ReviewSearchDto;
import com.team04.back.domain.weather.geo.service.GeoService;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.enums.Weather;
import com.team04.back.domain.weather.weather.repository.WeatherRepository;
import com.team04.back.domain.weather.weather.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(CommentControllerTest.TestConfig.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ReviewService reviewService;

    @Test
    @DisplayName("커멘트 다건 조회")
    public void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments")
                ).andDo(print());

        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);
        int size = comments.getContent().size();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size));

        for (int i = 0; i < size; i++) {
            Review comment = comments.getContent().get(i);
            resultActions
                    .andExpect(jsonPath("$.content[%d].email".formatted(i)).value(comment.getEmail()))
                    .andExpect(jsonPath("$.content[%d].imageUrl".formatted(i)).value(comment.getImageUrl()))
                    .andExpect(jsonPath("$.content[%d].title".formatted(i)).value(comment.getTitle()))
                    .andExpect(jsonPath("$.content[%d].sentence".formatted(i)).value(comment.getSentence()))
                    .andExpect(jsonPath("$.content[%d].tagString".formatted(i)).value(comment.getTagString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.location".formatted(i)).value(comment.getWeatherInfo().getLocation()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.date".formatted(i)).value(comment.getWeatherInfo().getDate().toString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.feelsLikeTemperature".formatted(i)).value(comment.getWeatherInfo().getFeelsLikeTemperature()));
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 위치, 날짜 필터링")
    public void t2_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments")
                                .param("location", "삿포로")
                                .param("date", "2025-01-01")
                ).andDo(print());

        ReviewSearchDto search = new ReviewSearchDto("삿포로", LocalDate.of(2025, 1, 1), null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);
        int size = comments.getContent().size();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isOk());

        for (int i = 0; i < size; i++) {
            Review comment = comments.getContent().get(i);
            resultActions
                    .andExpect(jsonPath("$.content[%d].id".formatted(i)).value(comment.getId()))
                    .andExpect(jsonPath("$.content[%d].email".formatted(i)).value(comment.getEmail()))
                    .andExpect(jsonPath("$.content[%d].imageUrl".formatted(i)).value(comment.getImageUrl()))
                    .andExpect(jsonPath("$.content[%d].title".formatted(i)).value(comment.getTitle()))
                    .andExpect(jsonPath("$.content[%d].sentence".formatted(i)).value(comment.getSentence()))
                    .andExpect(jsonPath("$.content[%d].tagString".formatted(i)).value(comment.getTagString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.location".formatted(i)).value(comment.getWeatherInfo().getLocation()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.date".formatted(i)).value(comment.getWeatherInfo().getDate().toString()));
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 위치, 체감 온도 필터링")
    public void t2_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments")
                                .param("location", "삿포로")
                                .param("feelsLikeTemperature", "-4.0")
                ).andDo(print());

        ReviewSearchDto search = new ReviewSearchDto("삿포로", null, -4.0, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);
        int size = comments.getContent().size();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isOk());

        for (int i = 0; i < size; i++) {
            Review comment = comments.getContent().get(i);
            resultActions
                    .andExpect(jsonPath("$.content[%d].id".formatted(i)).value(comment.getId()))
                    .andExpect(jsonPath("$.content[%d].email".formatted(i)).value(comment.getEmail()))
                    .andExpect(jsonPath("$.content[%d].imageUrl".formatted(i)).value(comment.getImageUrl()))
                    .andExpect(jsonPath("$.content[%d].title".formatted(i)).value(comment.getTitle()))
                    .andExpect(jsonPath("$.content[%d].sentence".formatted(i)).value(comment.getSentence()))
                    .andExpect(jsonPath("$.content[%d].tagString".formatted(i)).value(comment.getTagString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.location".formatted(i)).value(comment.getWeatherInfo().getLocation()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.feelsLikeTemperature".formatted(i)).value(comment.getWeatherInfo().getFeelsLikeTemperature()));
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 월 필터링")
    public void t2_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments")
                                .param("month", "1") // 1월 필터링
                ).andDo(print());

        ReviewSearchDto search = new ReviewSearchDto(null, null, null, 1, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);
        int size = comments.getContent().size();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size));

        for (int i = 0; i < size; i++) {
            Review comment = comments.getContent().get(i);
            resultActions
                    .andExpect(jsonPath("$.content[%d].id".formatted(i)).value(comment.getId()))
                    .andExpect(jsonPath("$.content[%d].email".formatted(i)).value(comment.getEmail()))
                    .andExpect(jsonPath("$.content[%d].imageUrl".formatted(i)).value(comment.getImageUrl()))
                    .andExpect(jsonPath("$.content[%d].title".formatted(i)).value(comment.getTitle()))
                    .andExpect(jsonPath("$.content[%d].sentence".formatted(i)).value(comment.getSentence()))
                    .andExpect(jsonPath("$.content[%d].tagString".formatted(i)).value(comment.getTagString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.location".formatted(i)).value(comment.getWeatherInfo().getLocation()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.date".formatted(i)).value(comment.getWeatherInfo().getDate().toString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.feelsLikeTemperature".formatted(i)).value(comment.getWeatherInfo().getFeelsLikeTemperature()));
        }
    }

    @Test
    @DisplayName("커멘트 조건 조회 - 이메일 필터링")
    public void t2_4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments")
                                .param("email", "user1@test.com") // 이메일 필터링
                ).andDo(print());

        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, "user1@test.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);
        int size = comments.getContent().size();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(size));

        for (int i = 0; i < size; i++) {
            Review comment = comments.getContent().get(i);
            resultActions
                    .andExpect(jsonPath("$.content[%d].id".formatted(i)).value(comment.getId()))
                    .andExpect(jsonPath("$.content[%d].email".formatted(i)).value(comment.getEmail()))
                    .andExpect(jsonPath("$.content[%d].imageUrl".formatted(i)).value(comment.getImageUrl()))
                    .andExpect(jsonPath("$.content[%d].title".formatted(i)).value(comment.getTitle()))
                    .andExpect(jsonPath("$.content[%d].sentence".formatted(i)).value(comment.getSentence()))
                    .andExpect(jsonPath("$.content[%d].tagString".formatted(i)).value(comment.getTagString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.location".formatted(i)).value(comment.getWeatherInfo().getLocation()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.date".formatted(i)).value(comment.getWeatherInfo().getDate().toString()))
                    .andExpect(jsonPath("$.content[%d].weatherInfoDto.feelsLikeTemperature".formatted(i)).value(comment.getWeatherInfo().getFeelsLikeTemperature()));
        }
    }

    @Test
    @DisplayName("커멘트 단건 조회")
    public void t3() throws Exception {
        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);

        int id = comments.getContent().get(0).getId();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments/" + id)
                ).andDo(print());

        Review comment = reviewService.findById(id).get();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.email").value(comment.getEmail()))
                .andExpect(jsonPath("$.imageUrl").value(comment.getImageUrl()))
                .andExpect(jsonPath("$.title").value(comment.getTitle()))
                .andExpect(jsonPath("$.sentence").value(comment.getSentence()))
                .andExpect(jsonPath("$.tagString").value(comment.getTagString()))
                .andExpect(jsonPath("$.weatherInfoDto.location").value(comment.getWeatherInfo().getLocation()))
                .andExpect(jsonPath("$.weatherInfoDto.date").value(comment.getWeatherInfo().getDate().toString()))
                .andExpect(jsonPath("$.weatherInfoDto.feelsLikeTemperature").value(comment.getWeatherInfo().getFeelsLikeTemperature()));
    }

    @Test
    @DisplayName("커멘트 단건 조회 - 존재하지 않는 ID")
    public void t3_1() throws Exception {
        int id = Integer.MAX_VALUE; // 존재하지 않는 ID

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/comments/" + id)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("getComment"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("커멘트 비밀번호 검증")
    public void t4() throws Exception {
        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);

        int id = comments.getContent().get(0).getId();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/comments/" + id + "/verify-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "password": "1234"
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("verifyPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("비밀번호가 일치합니다."))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("커멘트 비밀번호 검증 - 잘못된 비밀번호")
    public void t4_1() throws Exception {
        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);

        int id = comments.getContent().get(0).getId();

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/comments/" + id + "/verify-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "password": "wrong-password"
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("verifyPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("커멘트 삭제")
    public void t5() throws Exception {
        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);

        int id = comments.getContent().get(0).getId();

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/comments/" + id)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("deleteComment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 커멘트가 삭제되었습니다.".formatted(id)))
                .andExpect(jsonPath("$.data.id").value(id));
    }


    @Autowired
    private WeatherService weatherService;
    @Autowired
    private WeatherRepository weatherRepository;
    @Autowired
    private GeoService geoService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public WeatherService weatherService() {
            return Mockito.mock(WeatherService.class);
        }
        @Bean
        public GeoService geoService() {
            return Mockito.mock(GeoService.class);
        }
    }

    @BeforeEach
    void setUp() {
        WeatherInfo mockWeatherInfo = WeatherInfo.builder()
                .location("Seoul")
                .date(LocalDate.of(2025, 1, 1))
                .weather(Weather.CLEAR_SKY)
                .minTemperature(15.0)
                .maxTemperature(25.0)
                .dailyTemperatureGap(10.0)
                .feelsLikeTemperature(20.0)
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .build();

        WeatherInfo saved = weatherRepository.save(mockWeatherInfo);

        Mockito.when(geoService.getCoordinatesFromLocation(eq("Seoul"), eq("KR")))
                .thenReturn(List.of(37.5665, 126.9780));

        Mockito.when(weatherService.getWeatherInfo(eq("Seoul"), anyDouble(), anyDouble(), any(LocalDate.class)))
                .thenReturn(saved);
    }

    @Test
    @DisplayName("커멘트 작성")
    public void t6() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
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
                                        """)
                ).andDo(print());

        Review comment = reviewService.findLatest();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("createComment"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 커멘트가 작성되었습니다.".formatted(comment.getId())))
                .andExpect(jsonPath("$.data.id").value(comment.getId()))
                .andExpect(jsonPath("$.data.email").value(comment.getEmail()))
                .andExpect(jsonPath("$.data.imageUrl").value(comment.getImageUrl()))
                .andExpect(jsonPath("$.data.title").value(comment.getTitle()))
                .andExpect(jsonPath("$.data.sentence").value(comment.getSentence()))
                .andExpect(jsonPath("$.data.tagString").value(comment.getTagString()))
                .andExpect(jsonPath("$.data.weatherInfoDto.location").value(comment.getWeatherInfo().getLocation()))
                .andExpect(jsonPath("$.data.weatherInfoDto.date").value(comment.getWeatherInfo().getDate().toString()))
                .andExpect(jsonPath("$.data.weatherInfoDto.feelsLikeTemperature").value(comment.getWeatherInfo().getFeelsLikeTemperature()));
    }

    @Test
    @DisplayName("커멘트 작성 - inValid email")
    public void t6_1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
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
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("createComment"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        email-Email-must be a well-formed email address
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("커멘트 작성 - inValid title")
    public void t6_2() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
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
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("createComment"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("""
                        title-NotBlank-must not be blank
                        title-Size-size must be between 2 and 100
                        """.stripIndent().trim()));
    }

    @Test
    @DisplayName("커멘트 작성 - inValid date")
    public void t6_3() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
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
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("createComment"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("요청 본문이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("커멘트 수정")
    public void t7() throws Exception {
        ReviewSearchDto search = new ReviewSearchDto(null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> comments = reviewService.findBySearch(search, pageable);

        int id = comments.getContent().get(0).getId();

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/comments/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "Updated Title",
                                            "sentence": "This is an updated comment.",
                                            "tagString": "#updated#comment",
                                            "imageUrl": "http://example.com/updated_image.jpg",
                                            "countryCode": "KR",
                                            "cityName": "Seoul",
                                            "date": "2025-01-01"
                                        }
                                        """)
                ).andDo(print());

        Review comment = reviewService.findById(id).get();

        resultActions
                .andExpect(handler().handlerType(ReviewController.class))
                .andExpect(handler().methodName("modifyComment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 커멘트가 수정되었습니다.".formatted(comment.getId())))
                .andExpect(jsonPath("$.data.id").value(comment.getId()))
                .andExpect(jsonPath("$.data.email").value(comment.getEmail()))
                .andExpect(jsonPath("$.data.imageUrl").value(comment.getImageUrl()))
                .andExpect(jsonPath("$.data.title").value("Updated Title"))
                .andExpect(jsonPath("$.data.sentence").value("This is an updated comment."))
                .andExpect(jsonPath("$.data.tagString").value("#updated#comment"))
                .andExpect(jsonPath("$.data.weatherInfoDto.location").value(comment.getWeatherInfo().getLocation()))
                .andExpect(jsonPath("$.data.weatherInfoDto.date").value(comment.getWeatherInfo().getDate().toString()))
                .andExpect(jsonPath("$.data.weatherInfoDto.feelsLikeTemperature").value(comment.getWeatherInfo().getFeelsLikeTemperature()));
    }
}
