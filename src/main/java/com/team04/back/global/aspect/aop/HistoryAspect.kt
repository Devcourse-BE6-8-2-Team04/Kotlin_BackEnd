package com.team04.back.global.aspect.aop

import com.team04.back.domain.history.history.service.HistoryService
import com.team04.back.global.annotation.TrackHistory
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Component
@Aspect
class HistoryAspect(
    private val historyService: HistoryService,
    private val request: HttpServletRequest
) {

    @AfterReturning("@annotation(com.team04.back.global.annotation.TrackHistory)")
    fun trackMethodAction(joinPoint: JoinPoint) {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(TrackHistory::class.java) ?: return

        // X-Forwarded-For 헤더 먼저 확인
        val ipAddress = request.getHeader("X-Forwarded-For")
            ?.split(",")?.firstOrNull()?.trim()
            ?: request.remoteAddr

        val userId = null   // 추후 인증 기능 구현 시 변경 예정
        val latitude: Double? = null
        val longitude: Double? = null
        val path = annotation.action

        historyService.saveHistory(
            path,
            userId,
            ipAddress,
            latitude,
            longitude
        )
    }
}
