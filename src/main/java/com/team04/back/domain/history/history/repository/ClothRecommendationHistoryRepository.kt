package com.team04.back.domain.history.history.repository

import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ClothRecommendationHistoryRepository :JpaRepository<ClothRecommendationHistory, Int>{
    fun findByDateAndLocation(date: LocalDate, location: String): List<ClothRecommendationHistory>
    fun findByDateBetweenAndLocation(startDate: LocalDate, endDate: LocalDate, location: String): List<ClothRecommendationHistory>
}