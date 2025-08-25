package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.cloth.cloth.enums.Category;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryClothDto {

    private String clothName;
    private String imageUrl;
    private Category category;

    public CategoryClothDto(String clothName, String imageUrl, Category category) {
        this.clothName = clothName;
        this.imageUrl = imageUrl;
        this.category = category;
    }

}
