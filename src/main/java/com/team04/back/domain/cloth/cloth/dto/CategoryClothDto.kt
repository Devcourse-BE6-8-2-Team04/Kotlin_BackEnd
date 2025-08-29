package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.enums.Category

data class CategoryClothDto(
    val clothName: String,
    val imageUrl: String,
    val category: Category
)
