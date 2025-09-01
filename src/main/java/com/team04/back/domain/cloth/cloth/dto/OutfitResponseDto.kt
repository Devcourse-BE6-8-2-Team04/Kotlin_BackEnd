package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.Clothing
import com.team04.back.domain.cloth.cloth.enums.Style

data class OutfitResponseDto(
    val clothes: Map<Style, List<Clothing>>,
    val extraClothes: Map<Style, List<Clothing>>
) {
    constructor(outfits: Map<Style, List<Clothing?>?>) : this(
        outfits
            .filterKeys { it != Style.EXTRA }
            .mapValues { it.value?.filterNotNull() ?: emptyList() },
        outfits
            .filterKeys { it == Style.EXTRA }
            .mapValues { it.value?.filterNotNull() ?: emptyList() }
    )
}
