package com.team04.back.domain.review.review.repository

import com.team04.back.domain.review.review.entity.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Int>, ReviewRepositoryCustom {
    fun findFirstByOrderByIdDesc(): Review?
}
