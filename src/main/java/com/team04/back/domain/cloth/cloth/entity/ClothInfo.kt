package com.team04.back.domain.cloth.cloth.entity

import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Material
import com.team04.back.domain.cloth.cloth.enums.Style
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class ClothInfo(
    @Column(nullable = false)
    var clothName: ClothName,
    @Column(nullable = false)
    var imageUrl: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,

    @Enumerated(EnumType.STRING)
    var style: Style?,

    @Enumerated(EnumType.STRING)
    var material: Material?,

    var maxFeelsLike: Double?,

    var minFeelsLike: Double?,

) : BaseEntity() {

    protected constructor() : this(clothName = ClothName.T_SHIRT, imageUrl = "", category = Category.TOP, style = null, material = null, maxFeelsLike = null, minFeelsLike = null)

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
            require(!imageUrl.isBlank()) { "Image URL cannot be empty." }
            if (minFeelsLike != null && maxFeelsLike != null) {
                require(maxFeelsLike >= minFeelsLike) { "Max feels like temperature must be greater than or equal to min feels like temperature." }
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
