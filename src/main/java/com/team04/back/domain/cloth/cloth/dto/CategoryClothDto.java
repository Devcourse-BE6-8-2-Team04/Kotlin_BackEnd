package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.cloth.cloth.enums.Category;

public record CategoryClothDto(
    String clothName,
    String imageUrl,
    Category category
) {
}