package com.team04.back.domain.cloth.cloth.entity

import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.*

@Entity
class ClothInfo(
    @Column(nullable = false)
    override var clothName: String,

    @Column(nullable = false)
    override var imageUrl: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,

    @Column(nullable = false)
    var maxFeelsLike: Double,

    @Column(nullable = false)
    var minFeelsLike: Double
) : BaseEntity(), Clothing {

    protected constructor() : this("", "", Category.CASUAL_DAILY, 0.0, 0.0)

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
        @JvmStatic
        fun create(
            clothName: String?,
            imageUrl: String?,
            category: Category?,
            minFeelsLike: Double?,
            maxFeelsLike: Double?
        ): ClothInfo {
            require(!clothName.isNullOrBlank()) { "Cloth name cannot be empty." }
            require(!imageUrl.isNullOrBlank()) { "Image URL cannot be empty." }
            require(category != null) { "Category cannot be null." }
            require(minFeelsLike != null) { "Min feels like temperature cannot be null." }
            require(maxFeelsLike != null) { "Max feels like temperature cannot be null." }
            require(maxFeelsLike >= minFeelsLike) {
                "Max feels like temperature must be greater than or equal to min feels like temperature."
            }

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
