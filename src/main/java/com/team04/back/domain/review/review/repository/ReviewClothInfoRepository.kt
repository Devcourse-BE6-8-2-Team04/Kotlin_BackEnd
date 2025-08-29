package com.team04.back.domain.review.review.repository

import com.team04.back.domain.review.review.entity.ReviewClothInfo
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewClothInfoRepository : JpaRepository<ReviewClothInfo, Int> {
    fun findByReviewId(reviewId: Int): List<ReviewClothInfo>
}
