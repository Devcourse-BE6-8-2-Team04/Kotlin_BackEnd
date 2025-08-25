package com.team04.back.domain.cloth.cloth.entity;

import com.team04.back.domain.weather.weather.enums.Weather;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtraCloth implements Clothing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String clothName;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Weather weather;

    @Builder(access = AccessLevel.PRIVATE)
    private ExtraCloth(String clothName, String imageUrl, Weather weather) {
        this.clothName = clothName;
        this.imageUrl = imageUrl;
        this.weather = weather;
    }

    public static ExtraCloth create(String clothName, String imageUrl, Weather weather) {
        if (clothName == null || clothName.isBlank()) {
            throw new IllegalArgumentException("Cloth name cannot be empty.");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be empty.");
        }
        if (weather == null) {
            throw new IllegalArgumentException("Weather cannot be null.");
        }

        return ExtraCloth.builder()
                .clothName(clothName)
                .imageUrl(imageUrl)
                .weather(weather)
                .build();
    }

    public void update(String clothName, String imageUrl, Weather weather) {
        if (clothName != null && !clothName.isBlank()) {
            this.clothName = clothName;
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrl = imageUrl;
        }
        if (weather != null) {
            this.weather = weather;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraCloth that = (ExtraCloth) o;
        return clothName.equals(that.clothName) &&
                imageUrl.equals(that.imageUrl) &&
                weather == that.weather;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clothName, imageUrl, weather);
    }
}