package com.team04.back.domain.member.member.service

import com.team04.back.domain.member.member.entity.Member
import com.team04.back.standard.util.Ut
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AuthTokenService {

    @Value("\${custom.jwt.secretKey}")
    private lateinit var jwtSecretKey: String

    @Value("\${custom.accessToken.expirationSeconds}")
    private var accessTokenExpirationSeconds: Int = 0

    // AccessToken 생성
    fun genAccessToken(member: Member): String {
        // Map<String, Any> 타입 명시 + null-safe 처리
        val payload: Map<String, Any> = mapOf(
            "id" to (member.id ?: 0),
            "userId" to (member.userId ?: ""),
            "email" to (member.email ?: "")
        )
        return Ut.jwt.toString(jwtSecretKey, accessTokenExpirationSeconds, payload)
    }

    // AccessToken에서 Payload 추출
    fun payload(accessToken: String): Map<String, Any>? {
        val parsedPayload = Ut.jwt.payload(jwtSecretKey, accessToken) ?: return null

        val id = parsedPayload["id"] as? Int ?: 0
        val userId = parsedPayload["userId"] as? String ?: ""
        val email = parsedPayload["email"] as? String ?: ""

        return mapOf(
            "id" to id,
            "userId" to userId,
            "email" to email
        )
    }
}
