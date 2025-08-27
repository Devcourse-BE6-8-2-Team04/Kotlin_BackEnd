package com.team04.back.domain.cloth.cloth.entity;

import com.team04.back.domain.cloth.cloth.enums.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothInfo implements Clothing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String clothName;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Double maxFeelsLike;

    @Column(nullable = false)
    private Double minFeelsLike;

    @Builder(access = AccessLevel.PRIVATE)
    private ClothInfo(String clothName, String imageUrl, Category category, Double maxFeelsLike, Double minFeelsLike) {
        this.clothName = clothName;
        this.imageUrl = imageUrl;
        this.category = category;
        this.maxFeelsLike = maxFeelsLike;
        this.minFeelsLike = minFeelsLike;
    }

    public static ClothInfo create(String clothName, String imageUrl, Category category, Double minFeelsLike, Double maxFeelsLike) {
        if (clothName == null || clothName.isBlank()) {
            throw new IllegalArgumentException("Cloth name cannot be empty.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be empty.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (maxFeelsLike == null || minFeelsLike == null) {
            throw new IllegalArgumentException("Feels like temperatures cannot be null.");
        }
        if (maxFeelsLike < minFeelsLike) {
            throw new IllegalArgumentException("Max feels like temperature must be greater than or equal to min feels like temperature.");
        }
        return ClothInfo.builder()
                .clothName(clothName)
                .imageUrl(imageUrl)
                .category(category)
                .minFeelsLike(minFeelsLike)
                .maxFeelsLike(maxFeelsLike)
                .build();
    }

    public void update(String clothName, String imageUrl, Category category, Double maxFeelsLike, Double minFeelsLike) {
        if (clothName != null && !clothName.isBlank()) {
            this.clothName = clothName;
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrl = imageUrl;
        }
        if (category != null) {
            this.category = category;
        }
        if (maxFeelsLike != null) {
            this.maxFeelsLike = maxFeelsLike;
        }
        if (minFeelsLike != null) {
            this.minFeelsLike = minFeelsLike;
        }
    }
}