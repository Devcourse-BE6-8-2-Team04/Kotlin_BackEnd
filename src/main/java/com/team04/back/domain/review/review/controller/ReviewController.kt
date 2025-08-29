package com.team04.back.domain.review.review.controller

import com.team04.back.domain.review.review.dto.ReviewDto
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.service.ReviewService
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.service.WeatherService
import com.team04.back.global.rsData.RsData
import com.team04.back.standard.dto.PageDto
import com.team04.back.standard.dto.ReviewSearchDto
import com.team04.back.standard.dto.ReviewSearchSortType
import com.team04.back.standard.extensions.getOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "ReviewController", description = "리뷰 API")
class ReviewController(
    private val reviewService: ReviewService,
    private val weatherService: WeatherService,
    private val geoService: GeoService,
) {
    /**
     * 이 API는 location, date, feelsLikeTemperature, month 파라미터를 사용하여 필터링된 리뷰 목록을 조회합니다.
     * 필터링 조건이 없으면 전체 리뷰를 조회합니다.
     * 여행자 추천 : location + date 조합 또는 location + feelsLikeTemperature 조합
     * 검색 필터 : location + feelsLikeTemperature + month 조합 (3! = 6가지 조합)
     * @param location 위치 필터링
     * @param date 날짜 필터링
     * @param feelsLikeTemperature 체감 온도 필터링
     * @param month 월 필터링
     * @param page 페이지 번호(1부터 시작)
     * @param pageSize 페이지 크기
     * @param sort 정렬 기준
     * @return 리뷰 DTO 목록
     */
    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "리뷰 다건 조회", description = "필터링된 리뷰 목록을 조회합니다.")
    fun getReviews(
        @RequestParam location: String?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @RequestParam feelsLikeTemperature: Double?,
        @RequestParam month: Int?,
        @RequestParam email: String?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(defaultValue = "ID") sort: ReviewSearchSortType
    ): PageDto<ReviewDto> {
        val search = ReviewSearchDto(
            location,
            date,
            feelsLikeTemperature,
            month,
            email
        )

        val items: Page<Review> = reviewService.findBySearch(search, page, pageSize, sort)
        return PageDto(items.map { ReviewDto(it) })
    }

    /**
     * ID로 리뷰를 조회합니다.
     * @param id 리뷰 ID
     * @return 리뷰 DTO
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "리뷰 단건 조회", description = "ID로 리뷰를 조회합니다.")
    fun getReview(@PathVariable id: Int): ReviewDto {
        val review = reviewService.findById(id).getOrThrow()
        return ReviewDto(review)
    }


    data class VerifyPasswordReqBody(
        @field:NotBlank val password: String
    )

    /**
     * 리뷰의 비밀번호를 검증합니다.
     * @param id 리뷰 ID
     * @param passwordReqBody 비밀번호 요청 바디
     * @return 비밀번호 검증 결과
     */
    @PostMapping("/{id}/verify-password")
    @Transactional(readOnly = true)
    @Operation(summary = "리뷰 비밀번호 검증", description = "리뷰의 비밀번호를 검증합니다.")
    fun verifyPassword(
        @PathVariable id: Int,
        @RequestBody passwordReqBody: VerifyPasswordReqBody
    ): RsData<Boolean> {
        val review = reviewService.findById(id).getOrThrow()

        val isVerified = reviewService.verifyPassword(review, passwordReqBody.password)
        if (!isVerified) {
            return RsData("400-1", "비밀번호가 일치하지 않습니다.", false)
        }

        return RsData(
            "200-1",
            "비밀번호가 일치합니다.",
            true
        )
    }

    /**
     * 리뷰를 삭제합니다.
     * @param id 리뷰 ID
     * @return 리뷰 DTO
     */
    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    fun deleteReview(@PathVariable id: Int): RsData<ReviewDto> {
        val review = reviewService.findById(id).getOrThrow()

        reviewService.delete(review)

        return RsData(
            "200-1",
            "${id}번 리뷰가 삭제되었습니다.",
            ReviewDto(review)
        )
    }


    data class CreateReviewReqBody(
        @field:NotBlank @field:Email val email: String,
        @field:NotBlank @field:Size(min = 4) val password: String,
        @field:NotBlank @field:Size(min = 2, max = 100) val title: String,
        @field:NotBlank @field:Size(min = 2, max = 500) val sentence: String,
        val tagString: String?,
        val imageUrl: String?,
        @field:NotBlank val countryCode: String,
        @field:NotBlank val cityName: String,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate
    )

    /**
     * 리뷰를 작성합니다.
     * @param createReviewReqBody 리뷰 생성 요청 바디
     * @return 저장된 리뷰 DTO
     */
    @PostMapping
    @Transactional
    @Operation(summary = "리뷰 작성", description = "새로운 리뷰를 작성합니다.")
    fun createReview(
        @RequestBody @Valid createReviewReqBody: CreateReviewReqBody
    ): RsData<ReviewDto> {
        val coordinates = geoService.getCoordinatesFromLocation(
            createReviewReqBody.cityName,
            createReviewReqBody.countryCode
        )
        val weatherInfo = weatherService.getWeatherInfo(
            createReviewReqBody.cityName,
            coordinates[0],
            coordinates[1],
            createReviewReqBody.date
        )

        val review = reviewService.createReview(
            createReviewReqBody.email,
            createReviewReqBody.password,
            createReviewReqBody.imageUrl,
            createReviewReqBody.title,
            createReviewReqBody.sentence,
            createReviewReqBody.tagString,
            weatherInfo
        )

        return RsData(
            "201-1",
            "${review.id}번 리뷰가 작성되었습니다.",
            ReviewDto(review)
        )
    }


    data class ModifyReviewReqBody(
        @field:NotBlank @field:Size(min = 2, max = 100) val title: String,
        @field:NotBlank @field:Size(min = 2, max = 500) val sentence: String,
        val tagString: @NotBlank String?,
        val imageUrl: String?,
        @field:NotBlank val countryCode: String,
        @field:NotBlank val cityName: String,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate
    )

    /**
     * 리뷰를 수정합니다.
     * @param id 리뷰 ID
     * @param modifyReviewReqBody 리뷰 수정 요청 바디
     * @return 수정된 리뷰 DTO
     */
    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
    fun modifyReview(
        @PathVariable id: Int,
        @RequestBody @Valid modifyReviewReqBody: ModifyReviewReqBody
    ): RsData<ReviewDto> {
        var review = reviewService.findById(id).getOrThrow()

        val coordinates = geoService.getCoordinatesFromLocation(
            modifyReviewReqBody.cityName,
            modifyReviewReqBody.countryCode
        )
        val weatherInfo = weatherService.getWeatherInfo(
            modifyReviewReqBody.cityName,
            coordinates[0],
            coordinates[1],
            modifyReviewReqBody.date
        )

        review = reviewService.modify(
            review,
            modifyReviewReqBody.title,
            modifyReviewReqBody.sentence,
            modifyReviewReqBody.tagString,
            modifyReviewReqBody.imageUrl,
            weatherInfo
        )

        return RsData(
            "200-1",
            "${review.id}번 리뷰가 수정되었습니다.",
            ReviewDto(review)
        )
    }
}
