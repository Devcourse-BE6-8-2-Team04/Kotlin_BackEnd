package com.team04.back.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtProperties {

    @Value("\${custom.jwt.secretKey}")
    lateinit var jwtSecretKey: String

    @Value("\${custom.accessToken.expirationSeconds}")
    var accessTokenExpirationSeconds: Int = 0
}
