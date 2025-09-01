package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Style

data class OutfitResponseDto(
    val clothes: Map<Style, List<ClothInfo>>,
)
