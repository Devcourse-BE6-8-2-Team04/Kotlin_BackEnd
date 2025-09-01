package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Style

data class OutfitResponseDto(
    val clothes: Map<Style, List<ClothInfo>>,
    val extraClothes: Map<Style, List<ClothInfo>>
) {
    constructor(outfits: Map<Style, List<ClothInfo?>?>) : this(
        outfits
            .filterKeys { it != Style.EXTRA }
            .mapValues { it.value?.filterNotNull() ?: emptyList() },
        outfits
            .filterKeys { it == Style.EXTRA }
            .mapValues { it.value?.filterNotNull() ?: emptyList() }
    )
}
