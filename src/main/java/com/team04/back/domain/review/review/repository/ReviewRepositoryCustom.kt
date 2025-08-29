package com.team04.back.domain.review.review.repository

import com.team04.back.domain.review.review.entity.Review
import com.team04.back.standard.dto.ReviewSearchDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ReviewRepositoryCustom {
    fun findBySearch(
        search: ReviewSearchDto,
        pageable: Pageable
    ): Page<Review>
}
