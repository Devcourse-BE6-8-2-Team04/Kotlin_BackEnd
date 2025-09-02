package com.team04.back.domain.review.review.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class CreateReviewReqBody(
        @field:NotBlank @field:Email val email: String,
        @field:NotBlank @field:Size(min = 4) val password: String,
        @field:NotBlank @field:Size(min = 2, max = 100) val title: String,
        @field:NotBlank @field:Size(min = 2, max = 500) val sentence: String,
        val tagString: String?,
        val imageUrl: String?,
        @field:NotBlank val countryCode: String,
        @field:NotBlank val cityName: String,
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE) val date: LocalDate,
        @field:Valid val clothList: List<ClothItemReqBody>?
)