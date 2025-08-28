package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.ExtraCloth
import com.team04.back.domain.weather.weather.enums.Weather

@JvmRecord
data class ExtraClothDto(
    val id: Int,
    @JvmField val clothName: String,
    val imageUrl: String,
    val weather: Weather
) {
    constructor(extraCloth: ExtraCloth) : this(
        extraCloth.id,
        extraCloth.clothName,
        extraCloth.imageUrl,
        extraCloth.weather
    )
}