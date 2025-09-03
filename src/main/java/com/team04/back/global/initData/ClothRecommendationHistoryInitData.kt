package com.team04.back.global.initData

import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.repository.ClothRepository
import com.team04.back.domain.cloth.cloth.service.ClothService
import com.team04.back.domain.history.history.entity.ClothRecommendationHistory
import com.team04.back.domain.history.history.repository.ClothRecommendationHistoryRepository
import com.team04.back.domain.member.member.repository.MemberRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Configuration
class ClothRecommendationHistoryInitData(
    private val clothRecommendationHistoryRepository: ClothRecommendationHistoryRepository,
    private val memberRepository: MemberRepository,
    private val clothService: ClothService,  // ClothInfo 조회를 위해 추가
    private val clothRepository: ClothRepository
) {
    @Bean
    @Order(4)
    fun clothRecommendationHistoryInitDataRunner(): ApplicationRunner {
        return ApplicationRunner { insertData() }
    }

    @Transactional
    fun insertData() {
        if (clothRecommendationHistoryRepository.count() > 0) return

        val member = memberRepository.findByUserId("user1") ?: return

        val likedCloths = clothService.findByClothName(ClothName.SWEATER)

        val likedClothEntities = likedCloths.map { clothRepository.findById(it.id).orElseThrow() }

        val dislikedClothings = clothService.findByClothName(ClothName.LEATHER_BOOTS)

        val dislikedClothEntities = dislikedClothings.map { clothRepository.findById(it.id).orElseThrow() }


        val defaultHistory = ClothRecommendationHistory(
            member = member,
            location = "서울",
            date = LocalDate.now(),
            weatherInfo = emptyList(),
            likedClothings = likedClothEntities,
            dislikedClothings = dislikedClothEntities,
            feelsLike = 20.0,
            uvi = 5.0,
            rain = 0.0,
            snow = 0.0,
            humidity = 60,
            windSpeed = 3.5,
            tempMin = 15.0,
            tempMax = 22.0,
            dailyTemperatureGap = 7.0,
            reviewedAt = LocalDateTime.now()
        )

        clothRecommendationHistoryRepository.save(defaultHistory)
    }
}
