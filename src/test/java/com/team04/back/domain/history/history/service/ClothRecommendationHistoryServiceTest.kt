package com.team04.back.domain.history.history.service

import com.team04.back.common.fixture.FixtureFactory.createClothRecommendationHistory
import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import com.team04.back.domain.user.user.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.test.context.ActiveProfiles
import java.util.*
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.mockito.Mockito.verify
import org.mockito.kotlin.times

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class ClothRecommendationHistoryServiceTest {

    @InjectMocks
    lateinit var clothRecommendationHistoryService: ClothRecommendationHistoryService

    @Mock
    lateinit var clothRecommendationHistoryRepository: ClothRecommendationHistoryRepository

    @Test
    @DisplayName("의류 추천 기록 생성 테스트")
    fun createHistory() {
        // Given
        val user = User()
        val history = createClothRecommendationHistory(user, "Seoul", emptyList(), emptyList(), emptyList())
        whenever(clothRecommendationHistoryRepository.save(any<ClothRecommendationHistory>())).thenReturn(history)

        // When
        val createdHistory = clothRecommendationHistoryService.createHistory(history)

        // Then
        assertThat(createdHistory).isNotNull
        assertThat(createdHistory.user).isEqualTo(user)
    }

    @Test
    @DisplayName("ID로 의류 추천 기록 조회 테스트")
    fun getHistory() {
        // Given
        val user = User()
        val history = createClothRecommendationHistory(1, user, "Seoul", emptyList(), emptyList(), emptyList())
        whenever(clothRecommendationHistoryRepository.findById(any())).thenReturn(Optional.of(history))

        // When
        val foundHistory = clothRecommendationHistoryService.getHistory(1)

        // Then
        assertThat(foundHistory).isNotNull
        assertThat(foundHistory?.id).isEqualTo(1)
    }

    @Test
    @DisplayName("모든 의류 추천 기록 조회 테스트")
    fun getAllHistories() {
        // Given
        val user = User()
        val history1 = createClothRecommendationHistory(user, "Seoul", emptyList(), emptyList(), emptyList())
        val history2 = createClothRecommendationHistory(user, "Busan", emptyList(), emptyList(), emptyList())
        whenever(clothRecommendationHistoryRepository.findAll()).thenReturn(listOf(history1, history2))

        // When
        val histories = clothRecommendationHistoryService.getAllHistories()

        // Then
        assertThat(histories).hasSize(2)
    }

    @Test
    @DisplayName("의류 추천 기록 삭제 테스트")
    fun deleteHistory() {
        // Given
        val historyId = 1
        whenever(clothRecommendationHistoryRepository.existsById(historyId)).thenReturn(true)
        doNothing().whenever(clothRecommendationHistoryRepository).deleteById(historyId)

        // When
        clothRecommendationHistoryService.deleteHistory(historyId)

        // Then
        verify(clothRecommendationHistoryRepository, times(1)).deleteById(historyId)
    }

    @Test
    @DisplayName("존재하지 않는 ID로 기록 삭제 시 예외 발생 테스트")
    fun deleteHistory_notFound() {
        // Given
        val historyId = 1
        whenever(clothRecommendationHistoryRepository.existsById(historyId)).thenReturn(false)

        // When & Then
        assertThrows<IllegalArgumentException> {
            clothRecommendationHistoryService.deleteHistory(historyId)
        }
    }
}
