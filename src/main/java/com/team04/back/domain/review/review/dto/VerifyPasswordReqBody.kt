package com.team04.back.domain.review.review.dto

import jakarta.validation.constraints.NotBlank

data class VerifyPasswordReqBody(
        @field:NotBlank val password: String?
)