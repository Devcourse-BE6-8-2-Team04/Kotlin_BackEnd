package com.team04.back.domain.history.history.service

import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class ClothRecommendationHistoryService(
    private val clothRecommendationHistoryRepository: ClothRecommendationHistoryRepository
) {

    fun createHistory(history: ClothRecommendationHistory): ClothRecommendationHistory {
        return clothRecommendationHistoryRepository.save(history)
    }

    @Transactional(readOnly = true)
    fun getHistory(id: Int): ClothRecommendationHistory? {
        return clothRecommendationHistoryRepository.findByIdOrNull(id)
    }

    @Transactional(readOnly = true)
    fun getAllHistories(): List<ClothRecommendationHistory> {
        return clothRecommendationHistoryRepository.findAll()
    }

    fun deleteHistory(id: Int) {
        if (!clothRecommendationHistoryRepository.existsById(id)) {
            throw IllegalArgumentException("History not found with id: $id")
        }
        clothRecommendationHistoryRepository.deleteById(id)
    }
}