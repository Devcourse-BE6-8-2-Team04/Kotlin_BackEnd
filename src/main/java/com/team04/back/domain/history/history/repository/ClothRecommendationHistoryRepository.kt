package com.team04.back.domain.history.history.repository

import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClothRecommendationHistoryRepository :JpaRepository<ClothRecommendationHistory, Int>{
}