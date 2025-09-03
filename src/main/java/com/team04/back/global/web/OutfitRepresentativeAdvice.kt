package com.team04.back.global.web

import com.team04.back.domain.cloth.cloth.dto.WeatherClothResponseDto
import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import kotlin.math.abs

/**
 * ClothController.getClothDetails 응답(WeatherClothResponseDto)을
 * 클라이언트로 나가기 직전에 '대표 선택' 규칙으로 슬림화하는 어드바이스.
 *
 * - 서비스/컨트롤러 코드는 그대로 유지
 * - 체감기온 기반 점수화로 각 카테고리별 Top N만 남김(디폴트 1개)
 */
@ControllerAdvice
@Component
class OutfitRepresentativeAdvice : ResponseBodyAdvice<Any> {

    /** 카테고리별 최대 몇 개까지 노출할지 (요구사항: 1개면 1로) */
    private val LIMIT_PER_CATEGORY = 1

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        // WeatherClothResponseDto 응답만 후처리
        return returnType.parameterType == WeatherClothResponseDto::class.java
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        if (body !is WeatherClothResponseDto) return body

        val feels = body.weatherInfo?.feelsLikeTemperature
        if (feels == null) {
            // 체감기온이 없으면 그대로 반환
            return body
        }

        // 추천/비추천 각각을 대표만 남기도록 슬림화
        val slimRecommended = body.recommendedOutfits
            .mapValues { (_, list) -> pickTopByTempScore(list, feels, Mode.RECOMMENDED, LIMIT_PER_CATEGORY) }
            .filterValues { it.isNotEmpty() } // 빈 카테고리는 제거(원하면 유지 가능)

        val slimNotRecommended = body.notRecommendedOutfits
            .mapValues { (_, list) -> pickTopByTempScore(list, feels, Mode.NOT, LIMIT_PER_CATEGORY) }
            .filterValues { it.isNotEmpty() }

        return body.copy(
            recommendedOutfits = slimRecommended,
            notRecommendedOutfits = slimNotRecommended
        )
    }

    // --------------------------
    // 점수화 & 상위 N개 선택
    // --------------------------

    private enum class Mode { RECOMMENDED, NOT }

    private fun pickTopByTempScore(
        items: List<ClothInfo>,
        feels: Double,
        mode: Mode,
        limit: Int
    ): List<ClothInfo> {
        if (items.isEmpty()) return emptyList()

        return items
            .map { it to score(it, feels, mode) }
            .filter { (_, score) -> score > 0.0 }           // 의미 없는 후보 제거
            .sortedByDescending { it.second }                       // 점수 높은 순
            .take(limit)
            .map { it.first }
    }

    private fun score(item: ClothInfo, feels: Double, mode: Mode): Double {
        val min = item.minFeelsLike
        val max = item.maxFeelsLike

        return when (mode) {
            // 추천: 온도 범위 안에 있을수록(중앙에 가까울수록) 점수↑
            Mode.RECOMMENDED -> {
                if (min != null && max != null && feels in min..max) {
                    val mid = (min + max) / 2.0
                    100.0 - abs(feels - mid)                 // 중앙에 가까울수록 높음
                } else if (min == null || max == null) {
                    10.0                                     // 범용(후순위)
                } else 0.0                                    // 범위 밖
            }
            // 비추천: 온도 범위를 벗어날수록 점수↑
            Mode.NOT -> {
                val dist = tempDistance(item, feels) ?: 5.0  // 범용은 낮게
                if (dist > 0) 50.0 + dist else 0.0
            }
        }
    }

    private fun tempDistance(item: ClothInfo, feels: Double): Double? {
        val min = item.minFeelsLike
        val max = item.maxFeelsLike
        if (min == null || max == null) return null
        return when {
            feels < min -> (min - feels)
            feels > max -> (feels - max)
            else -> 0.0
        }
    }
}
