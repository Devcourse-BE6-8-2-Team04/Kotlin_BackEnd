package com.team04.back.domain.review.review.entity;

import com.team04.back.domain.weather.weather.entity.WeatherInfo;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 2048)
    private String imageUrl;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sentence;

    @Column(nullable = false)
    private String tagString;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "weather_info_id", nullable = false)
    private WeatherInfo weatherInfo;

    public Review(String email, String password, String imageUrl, String title, String sentence, String tagString, WeatherInfo weatherInfo) {
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.title = title;
        this.sentence = sentence;
        this.tagString = tagString;
        this.weatherInfo = weatherInfo;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSentence() {
        return sentence;
    }

    public String getTagString() {
        return tagString;
    }

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public Review modify(String title, String sentence, String tagString, String imageUrl, WeatherInfo weatherInfo) {
        this.title = title;
        this.sentence = sentence;
        this.tagString = tagString;
        this.imageUrl = imageUrl;
        this.weatherInfo = weatherInfo;

        return this;
    }
}
