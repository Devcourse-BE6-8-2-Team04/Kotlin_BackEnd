package com.team04.back.domain.review.review.entity

import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "review_cloth_info")
class ReviewClothInfo(
    reviewId: Int,
    clothInfoId: Int,
    isRecommend: Boolean
) : BaseEntity() {
    @Column(name = "review_id", nullable = false)
    val reviewId: Int = reviewId

    @Column(name = "cloth_info_id", nullable = false)
    val clothInfoId: Int = clothInfoId

    @Column(name = "is_recommend", nullable = false)
    var isRecommend: Boolean = isRecommend
}
