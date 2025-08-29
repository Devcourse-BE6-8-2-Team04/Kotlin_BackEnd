package com.team04.back.domain.cloth.cloth.repository

import com.team04.back.domain.cloth.cloth.entity.ExtraCloth
import com.team04.back.domain.weather.weather.enums.Weather
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExtraClothRepository : JpaRepository<ExtraCloth, Int> {
    fun findDistinctByWeather(weather: Weather): Set<ExtraCloth>
}
