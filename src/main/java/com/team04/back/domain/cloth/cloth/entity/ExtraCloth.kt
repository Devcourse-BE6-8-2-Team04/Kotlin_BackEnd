package com.team04.back.domain.cloth.cloth.entity

import com.team04.back.domain.weather.weather.enums.Weather
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class ExtraCloth(
    @Column(nullable = false)
    var clothName: String,

    @Column(nullable = false)
    var imageUrl: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var weather: Weather,

    ) : BaseEntity(), Clothing {

    fun update(
        clothName: String? = null,
        imageUrl: String? = null,
        weather: Weather? = null
    ) {
        clothName?.takeIf { it.isNotBlank() }?.let { this.clothName = it }
        imageUrl?.takeIf { it.isNotBlank() }?.let { this.imageUrl = it }
        weather?.let { this.weather = it }
    }

    companion object {
        fun create(
            clothName: String,
            imageUrl: String,
            weather: Weather
        ): ExtraCloth {
            require(clothName.isNotBlank()) { "Cloth name cannot be empty." }
            require(imageUrl.isNotBlank()) { "Image URL cannot be empty." }

            return ExtraCloth(
                clothName = clothName,
                imageUrl = imageUrl,
                weather = weather
            )
        }
    }
}