package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.Clothing
import com.team04.back.domain.cloth.cloth.enums.Category

@JvmRecord
data class OutfitResponseDto(
    val clothes: Map<Category, List<Clothing>>,
    val extraClothes: Map<Category, List<Clothing>>
) {
    constructor(outfits: Map<Category, List<Clothing?>?>) : this(
        outfits
            .filterKeys { it != Category.EXTRA }
            .mapValues { it.value?.filterNotNull() ?: emptyList() },
        outfits
            .filterKeys { it == Category.EXTRA }
            .mapValues { it.value?.filterNotNull() ?: emptyList() }
    )

}
