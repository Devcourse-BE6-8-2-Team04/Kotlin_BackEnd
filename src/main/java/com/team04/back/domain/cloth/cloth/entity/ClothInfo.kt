package com.team04.back.domain.cloth.cloth.entity

import com.team04.back.domain.cloth.cloth.enums.Category
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class ClothInfo(
    override var clothName: String,
    override var imageUrl: String,
    @Enumerated(EnumType.STRING)
    var category: Category,
    var maxFeelsLike: Double,
    var minFeelsLike: Double
) : Clothing(clothName, imageUrl) {

    protected constructor() : this(ClothName.T_SHIRT, "", Category.TOP, null, null, null, null)

    fun update(
        clothName: ClothName,
        imageUrl: String? = null,
        category: Category,
        style: Style? = null,
        material: Material? = null,
        minFeelsLike: Double? = null,
        maxFeelsLike: Double? = null
    ) {
        if (minFeelsLike != null && maxFeelsLike != null) {
            require(maxFeelsLike >= minFeelsLike) { "Max feels like temperature must be greater than or equal to min feels like temperature." }
        }

        this.clothName = clothName
        this.category = category

        imageUrl?.takeIf { it.isNotBlank() }?.let { this.imageUrl = it }
        style?.let { this.style = it }
        material?.let { this.material = it }
        maxFeelsLike?.let { this.maxFeelsLike = it }
        minFeelsLike?.let { this.minFeelsLike = it }
    }

    companion object {
        @JvmStatic
        fun create(
            clothName: ClothName,
            imageUrl: String,
            category: Category,
            style: Style?,
            material: Material?,
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
                style = style,
                material = material,
                minFeelsLike = minFeelsLike,
                maxFeelsLike = maxFeelsLike
            )
        }
    }
}
