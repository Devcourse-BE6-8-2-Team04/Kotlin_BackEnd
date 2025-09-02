package com.team04.back.domain.cloth.cloth.dto

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.Category
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Material
import com.team04.back.domain.cloth.cloth.enums.Style

data class CategoryClothDto(
    val clothName: ClothName,
    val imageUrl: String,
    val category: Category,
    val style: Style?,
    val material: Material?,
)  {
    companion object {
        fun from(clothInfo: ClothInfo): CategoryClothDto {
            return CategoryClothDto(
                clothInfo.clothName,
                clothInfo.imageUrl,
                clothInfo.category,
                clothInfo.style,
                clothInfo.material
            )
        }
    }
}