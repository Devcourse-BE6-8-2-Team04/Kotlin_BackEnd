package com.team04.back.domain.cloth.cloth.entity

import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class ClothInfo(
    @Column(nullable = false)
    var clothName: String,

    @Column(nullable = false)
    var imageUrl: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,

    @Column(nullable = false)
    var maxFeelsLike: Double,

    @Column(nullable = false)
    var minFeelsLike: Double,

    ) : BaseEntity(), Clothing {

    fun update(
        clothName: String? = null,
        imageUrl: String? = null,
        category: Category? = null,
        maxFeelsLike: Double? = null,
        minFeelsLike: Double? = null
    ) {
        clothName?.takeIf { it.isNotBlank() }?.let { this.clothName = it }
        imageUrl?.takeIf { it.isNotBlank() }?.let { this.imageUrl = it }
        category?.let { this.category = it }
        maxFeelsLike?.let { this.maxFeelsLike = it }
        minFeelsLike?.let { this.minFeelsLike = it }
    }

    companion object {
        fun create(
            clothName: String,
            imageUrl: String,
            category: Category,
            minFeelsLike: Double,
            maxFeelsLike: Double
        ): ClothInfo {
            require(clothName.isNotBlank()) { "Cloth name cannot be empty." }
            require(imageUrl.isNotBlank()) { "Image URL cannot be empty." }
            require(maxFeelsLike >= minFeelsLike) { "Max feels like temperature must be greater than or equal to min feels like temperature." }

            return ClothInfo(
                clothName = clothName,
                imageUrl = imageUrl,
                category = category,
                minFeelsLike = minFeelsLike,
                maxFeelsLike = maxFeelsLike
            )
        }
    }
}