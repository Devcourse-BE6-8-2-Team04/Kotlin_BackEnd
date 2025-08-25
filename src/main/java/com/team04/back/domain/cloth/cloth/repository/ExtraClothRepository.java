package com.team04.back.domain.cloth.cloth.repository;

import com.team04.back.domain.cloth.cloth.entity.ExtraCloth;
import com.team04.back.domain.weather.weather.enums.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ExtraClothRepository extends JpaRepository<ExtraCloth, Integer> {
    Set<ExtraCloth> findDistinctByWeather(Weather weather);
}
