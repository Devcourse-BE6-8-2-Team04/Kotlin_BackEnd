package com.team04.back.domain.cloth.cloth.dto;

import com.team04.back.domain.cloth.cloth.entity.Clothing;
import com.team04.back.domain.cloth.cloth.enums.Category;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record OutfitResponse(
        Map<Category, List<Clothing>> clothes,
        Map<Category, List<Clothing>> extraClothes
) {
    public OutfitResponse(Map<Category, List<Clothing>> outfits) {
        this(
            outfits.entrySet().stream()
                   .filter(entry -> entry.getKey() != Category.EXTRA)
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            outfits.entrySet().stream()
                   .filter(entry -> entry.getKey() == Category.EXTRA)
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
