package com.team04.back.global.initData


import com.team04.back.domain.cloth.cloth.entity.ClothInfo
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
    private val clothService: ClothService,
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

        fun resolve(vararg names: ClothName): List<ClothInfo> =
            names.flatMap { clothService.findByClothName(it) }
                .map { clothRepository.findById(it.id).orElseThrow() }
                .distinctBy { it.id }  // ✅ ID 기준 중복 제거


        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)
        val threeDaysAgo = today.minusDays(3)
        val now = LocalDateTime.now()

        // 1) 오늘: 캐주얼 - 더움 (28~32℃)
        clothRecommendationHistoryRepository.save(
            ClothRecommendationHistory(
                member = member,
                location = "서울",
                date = today,
                weatherInfo = emptyList(),
                likedClothings = resolve(ClothName.T_SHIRT, ClothName.SHORTS, ClothName.SANDALS),
                dislikedClothings = resolve(ClothName.SWEATER, ClothName.LEATHER_BOOTS),
                feelsLike = 30.0,
                uvi = 8.0,
                rain = 0.0,
                snow = 0.0,
                humidity = 55,
                windSpeed = 2.5,
                tempMin = 27.0,
                tempMax = 33.0,
                dailyTemperatureGap = 6.0,
                reviewedAt = now
            )
        )

        // 2) 어제: 오피스 - 따뜻 (18~22℃)
        clothRecommendationHistoryRepository.save(
            ClothRecommendationHistory(
                member = member,
                location = "서울",
                date = yesterday,
                weatherInfo = emptyList(),
                likedClothings = resolve(ClothName.DRESS_SHIRT, ClothName.SLACKS, ClothName.LOAFERS),
                dislikedClothings = resolve(ClothName.SANDALS),
                feelsLike = 20.0,
                uvi = 5.0,
                rain = 0.0,
                snow = 0.0,
                humidity = 45,
                windSpeed = 3.0,
                tempMin = 18.0,
                tempMax = 22.0,
                dailyTemperatureGap = 4.0,
                reviewedAt = now
            )
        )

        // 3) 그제: 아웃도어 - 추움 (-5~2℃)
        clothRecommendationHistoryRepository.save(
            ClothRecommendationHistory(
                member = member,
                location = "서울",
                date = twoDaysAgo,
                weatherInfo = emptyList(),
                likedClothings = resolve(ClothName.PADDING, ClothName.SKI_PANTS, ClothName.HIKING_SHOES),
                dislikedClothings = resolve(ClothName.SANDALS),
                feelsLike = -1.0,
                uvi = 1.0,
                rain = 0.0,
                snow = 4.0,
                humidity = 70,
                windSpeed = 5.0,
                tempMin = -4.0,
                tempMax = 2.0,
                dailyTemperatureGap = 6.0,
                reviewedAt = now
            )
        )

        // 4) 사흘 전: 데이트룩 - 따뜻 (18~22℃)
        clothRecommendationHistoryRepository.save(
            ClothRecommendationHistory(
                member = member,
                location = "서울",
                date = threeDaysAgo,
                weatherInfo = emptyList(),
                likedClothings = resolve(ClothName.CARDIGAN, ClothName.JEANS, ClothName.FLATS),
                dislikedClothings = resolve(ClothName.FUR_BOOTS),
                feelsLike = 19.0,
                uvi = 4.0,
                rain = 0.0,
                snow = 0.0,
                humidity = 50,
                windSpeed = 2.0,
                tempMin = 17.0,
                tempMax = 22.0,
                dailyTemperatureGap = 5.0,
                reviewedAt = now
            )
        )
    }
}
