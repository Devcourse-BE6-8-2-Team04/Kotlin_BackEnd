package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.enums.Category

@JvmRecord
data class CategoryClothDto(
    @JvmField val clothName: String,
    val imageUrl: String,
    val category: Category
) 