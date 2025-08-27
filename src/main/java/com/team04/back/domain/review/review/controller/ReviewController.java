package com.team04.back.domain.review.review.controller;

import com.team04.back.domain.review.review.dto.ReviewDto;
import com.team04.back.domain.review.review.entity.Review;
import com.team04.back.domain.review.review.service.ReviewService;
import com.team04.back.domain.review.review.dto.ReviewSearchDto;
import com.team04.back.domain.weather.geo.service.GeoService;
import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import com.team04.back.domain.weather.weather.service.WeatherService;
import com.team04.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "CommentController", description = "API 커멘트 컨트롤러")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final WeatherService weatherService;
    private final GeoService geoService;

    /**
     * 이 API는 location, date, feelsLikeTemperature, month 파라미터를 사용하여 필터링된 커멘트 목록을 조회합니다.
     * 필터링 조건이 없으면 전체 커멘트를 조회합니다.
     * 여행자 추천 : location + date 조합 또는 location + feelsLikeTemperature 조합
     * 검색 필터 : location + feelsLikeTemperature + month 조합 (3! = 6가지 조합)
     * @param location 위치 필터링
     * @param date 날짜 필터링
     * @param feelsLikeTemperature 체감 온도 필터링
     * @param month 월 필터링
     * @param pageable 페이지 정보
     * @return 커멘트 DTO 목록
     */
    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "커멘트 다건 조회", description = "필터링된 커멘트 목록을 조회합니다.")
    public Page<ReviewDto> getComments(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double feelsLikeTemperature,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String email,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ReviewSearchDto search = new ReviewSearchDto(
                location,
                date,
                feelsLikeTemperature,
                month,
                email
        );

        Page<Review> items = reviewService.findBySearch(search, pageable);
        return items.map(ReviewDto::new);
    }

    /**
     * ID로 커멘트를 조회합니다.
     * @param id 커멘트 ID
     * @return 커멘트 DTO
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "커멘트 단건 조회", description = "ID로 커멘트를 조회합니다.")
    public ReviewDto getComment(@PathVariable int id) {
        Review comment = reviewService.findById(id).get();
        return new ReviewDto(comment);
    }


    record verifyPasswordReqBody(
            @NonNull String password
    ) {}

    /**
     * 커멘트의 비밀번호를 검증합니다.
     * @param id 커멘트 ID
     * @param passwordReqBody 비밀번호 요청 바디
     * @return 비밀번호 검증 결과
     */
    @PostMapping("/{id}/verify-password")
    @Transactional(readOnly = true)
    @Operation(summary = "커멘트 비밀번호 검증", description = "커멘트의 비밀번호를 검증합니다.")
    public RsData<Boolean> verifyPassword(
            @PathVariable int id,
            @RequestBody @NonNull verifyPasswordReqBody passwordReqBody
    ) {
        Review comment = reviewService.findById(id).get();

        boolean isVerified = reviewService.verifyPassword(comment, passwordReqBody.password());
        if (!isVerified) {
            return new RsData<>("400-1", "비밀번호가 일치하지 않습니다.", false);
        }

        return new RsData<>(
                "200-1",
                "비밀번호가 일치합니다.",
                true
        );
    }

    /**
     * 커멘트를 삭제합니다.
     * @param id 커멘트 ID
     * @return 커멘트 DTO
     */
    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "커멘트 삭제", description = "커멘트를 삭제합니다.")
    public RsData<ReviewDto> deleteComment(@PathVariable int id) {
        Review comment = reviewService.findById(id).get();

        reviewService.delete(comment);

        return new RsData<>(
                "200-1",
                "%d번 커멘트가 삭제되었습니다.".formatted(id),
                new ReviewDto(comment)
        );
    }


    record CreateCommentReqBody(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 4) String password,
            @NotBlank @Size(min = 2, max = 100) String title,
            @NotBlank @Size(min = 2, max = 500) String sentence,
            @NotBlank String tagString,
            String imageUrl,
            @NotBlank String countryCode,
            @NotBlank String cityName,
            @NonNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {}

    /**
     * 커멘트를 작성합니다.
     * @param createCommentReqBody 커멘트 생성 요청 바디
     * @return 저장된 커멘트 DTO
     */
    @PostMapping
    @Transactional
    @Operation(summary = "커멘트 작성", description = "새로운 커멘트를 작성합니다.")
    public RsData<ReviewDto> createComment(
            @RequestBody @Valid CreateCommentReqBody createCommentReqBody
    ) {
        List<Double> coordinates = geoService.getCoordinatesFromLocation(
                createCommentReqBody.cityName,
                createCommentReqBody.countryCode
        );
        WeatherInfo weatherInfo = weatherService.getWeatherInfo(
                createCommentReqBody.cityName,
                coordinates.get(0),
                coordinates.get(1),
                createCommentReqBody.date
        );

        Review comment = reviewService.createComment(
                createCommentReqBody.email(),
                createCommentReqBody.password(),
                createCommentReqBody.imageUrl(),
                createCommentReqBody.title(),
                createCommentReqBody.sentence(),
                createCommentReqBody.tagString(),
                weatherInfo
        );

        return new RsData<>(
                "201-1",
                "%d번 커멘트가 작성되었습니다.".formatted(comment.getId()),
                new ReviewDto(comment)
        );
    }


    record ModifyCommentReqBody(
            @NotBlank @Size(min = 2, max = 100) String title,
            @NotBlank @Size(min = 2, max = 500) String sentence,
            @NotBlank String tagString,
            String imageUrl,
            @NotBlank String countryCode,
            @NotBlank String cityName,
            @NonNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {}

    /**
     * 커멘트를 수정합니다.
     * @param id 커멘트 ID
     * @param modifyCommentReqBody 커멘트 수정 요청 바디
     * @return 수정된 커멘트 DTO
     */
    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "커멘트 수정", description = "커멘트를 수정합니다.")
    public RsData<ReviewDto> modifyComment(
            @PathVariable int id,
            @RequestBody @Valid ModifyCommentReqBody modifyCommentReqBody
    ) {
        Review comment = reviewService.findById(id).get();

        List<Double> coordinates = geoService.getCoordinatesFromLocation(
                modifyCommentReqBody.cityName(),
                modifyCommentReqBody.countryCode()
        );
        WeatherInfo weatherInfo = weatherService.getWeatherInfo(
                modifyCommentReqBody.cityName(),
                coordinates.get(0),
                coordinates.get(1),
                modifyCommentReqBody.date()
        );

        comment = reviewService.modify(
                comment,
                modifyCommentReqBody.title(),
                modifyCommentReqBody.sentence(),
                modifyCommentReqBody.tagString(),
                modifyCommentReqBody.imageUrl(),
                weatherInfo
        );

        return new RsData<>(
                "200-1",
                "%d번 커멘트가 수정되었습니다.".formatted(comment.getId()),
                new ReviewDto(comment)
        );
    }
}
