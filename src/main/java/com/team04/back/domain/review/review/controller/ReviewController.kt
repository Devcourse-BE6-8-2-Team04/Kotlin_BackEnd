package com.team04.back.domain.review.review.controller

import com.team04.back.domain.review.review.dto.ReviewDto
import com.team04.back.domain.review.review.dto.ReviewSearchDto
import com.team04.back.domain.review.review.entity.Review
import com.team04.back.domain.review.review.service.ReviewService
import com.team04.back.domain.weather.geo.service.GeoService
import com.team04.back.domain.weather.weather.service.WeatherService
import com.team04.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "CommentController", description = "API 커멘트 컨트롤러")
class ReviewController(
    private val reviewService: ReviewService,
    private val weatherService: WeatherService,
    private val geoService: GeoService,
) {
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
    fun getComments(
        @RequestParam location: String?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @RequestParam feelsLikeTemperature: Double?,
        @RequestParam month: Int?,
        @RequestParam email: String?,
        @PageableDefault(size = 10, page = 0, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ReviewDto> {
        val search = ReviewSearchDto(
            location,
            date,
            feelsLikeTemperature,
            month,
            email
        )

        val items: Page<Review> = reviewService.findBySearch(search, pageable)
        return items.map { ReviewDto(it) }
    }

    /**
     * ID로 커멘트를 조회합니다.
     * @param id 커멘트 ID
     * @return 커멘트 DTO
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "커멘트 단건 조회", description = "ID로 커멘트를 조회합니다.")
    fun getComment(@PathVariable id: Int): ReviewDto {
        val comment = reviewService.findById(id).get()
        return ReviewDto(comment)
    }


    data class VerifyPasswordReqBody(
        @field:NotBlank val password: String
    )

    /**
     * 커멘트의 비밀번호를 검증합니다.
     * @param id 커멘트 ID
     * @param passwordReqBody 비밀번호 요청 바디
     * @return 비밀번호 검증 결과
     */
    @PostMapping("/{id}/verify-password")
    @Transactional(readOnly = true)
    @Operation(summary = "커멘트 비밀번호 검증", description = "커멘트의 비밀번호를 검증합니다.")
    fun verifyPassword(
        @PathVariable id: Int,
        @RequestBody passwordReqBody: VerifyPasswordReqBody
    ): RsData<Boolean> {
        val comment = reviewService.findById(id).get()

        val isVerified = reviewService.verifyPassword(comment, passwordReqBody.password)
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
     * 커멘트를 삭제합니다.
     * @param id 커멘트 ID
     * @return 커멘트 DTO
     */
    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "커멘트 삭제", description = "커멘트를 삭제합니다.")
    fun deleteComment(@PathVariable id: Int): RsData<ReviewDto> {
        val comment = reviewService.findById(id).get()

        reviewService.delete(comment)

        return RsData(
            "200-1",
            "${id}번 커멘트가 삭제되었습니다.",
            ReviewDto(comment)
        )
    }


    data class CreateCommentReqBody(
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
     * 커멘트를 작성합니다.
     * @param createCommentReqBody 커멘트 생성 요청 바디
     * @return 저장된 커멘트 DTO
     */
    @PostMapping
    @Transactional
    @Operation(summary = "커멘트 작성", description = "새로운 커멘트를 작성합니다.")
    fun createComment(
        @RequestBody @Valid createCommentReqBody: CreateCommentReqBody
    ): RsData<ReviewDto> {
        val coordinates = geoService.getCoordinatesFromLocation(
            createCommentReqBody.cityName,
            createCommentReqBody.countryCode
        )
        val weatherInfo = weatherService.getWeatherInfo(
            createCommentReqBody.cityName,
            coordinates[0],
            coordinates[1],
            createCommentReqBody.date
        )

        val comment = reviewService.createComment(
            createCommentReqBody.email,
            createCommentReqBody.password,
            createCommentReqBody.imageUrl,
            createCommentReqBody.title,
            createCommentReqBody.sentence,
            createCommentReqBody.tagString,
            weatherInfo
        )

        return RsData(
            "201-1",
            "${comment.id}번 커멘트가 작성되었습니다.",
            ReviewDto(comment)
        )
    }


    data class ModifyCommentReqBody(
        @field:NotBlank @field:Size(min = 2, max = 100) val title: String,
        @field:NotBlank @field:Size(min = 2, max = 500) val sentence: String,
        val tagString: @NotBlank String?,
        val imageUrl: String?,
        @field:NotBlank val countryCode: String,
        @field:NotBlank val cityName: String,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate
    )

    /**
     * 커멘트를 수정합니다.
     * @param id 커멘트 ID
     * @param modifyCommentReqBody 커멘트 수정 요청 바디
     * @return 수정된 커멘트 DTO
     */
    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "커멘트 수정", description = "커멘트를 수정합니다.")
    fun modifyComment(
        @PathVariable id: Int,
        @RequestBody @Valid modifyCommentReqBody: ModifyCommentReqBody
    ): RsData<ReviewDto> {
        var comment = reviewService.findById(id).get()

        val coordinates = geoService.getCoordinatesFromLocation(
            modifyCommentReqBody.cityName,
            modifyCommentReqBody.countryCode
        )
        val weatherInfo = weatherService.getWeatherInfo(
            modifyCommentReqBody.cityName,
            coordinates[0],
            coordinates[1],
            modifyCommentReqBody.date
        )

        comment = reviewService.modify(
            comment,
            modifyCommentReqBody.title,
            modifyCommentReqBody.sentence,
            modifyCommentReqBody.tagString,
            modifyCommentReqBody.imageUrl,
            weatherInfo
        )

        return RsData(
            "200-1",
            "${comment.id}번 커멘트가 수정되었습니다.",
            ReviewDto(comment)
        )
    }
}
