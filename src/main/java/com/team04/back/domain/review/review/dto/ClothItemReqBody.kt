package com.team04.back.domain.review.review.dto

import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Material
import com.team04.back.domain.cloth.cloth.enums.Style

data class ClothItemReqBody(
    val clothName: ClothName,
    val category: Category,
    val style: Style? = null,
    val material: Material? = null,
    val isRecommend: Boolean,
)