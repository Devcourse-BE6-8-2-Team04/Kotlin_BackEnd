package com.team04.back.domain.review.review.entity

import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "review_cloth_info")
class ReviewClothInfo(
    @field:Column(name = "review_id") val reviewId: Int,
    @field:Column(name = "cloth_info_id") val clothInfoId: Int,
    @field:Column(name = "is_recommend") var isRecommend: Boolean
) : BaseEntity() {
    fun toggleRecommend(isRecommend: Boolean): ReviewClothInfo {
        this.isRecommend = !isRecommend
        return this
    }

    companion object {
        fun of(reviewId: Int, clothInfoId: Int, isRecommend: Boolean): ReviewClothInfo {
            return ReviewClothInfo(reviewId, clothInfoId, isRecommend)
        }
    }
}
