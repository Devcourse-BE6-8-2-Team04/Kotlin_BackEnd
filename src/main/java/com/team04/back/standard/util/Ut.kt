package com.team04.back.standard.util

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.*

object Ut {

    object jwt {

        fun toString(secret: String, expireSeconds: Int, body: Map<String, Any>): String {
            val claims: Claims = Jwts.claims().apply {
                body.forEach { (key, value) ->
                    this[key] = value
                }
            }

            val issuedAt = Date()
            val expiration = Date(issuedAt.time + expireSeconds * 1000L)
            val secretKey: Key = Keys.hmacShaKeyFor(secret.toByteArray())

            return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact()
        }

        fun isValid(secret: String, jwtStr: String): Boolean {
            val secretKey: Key = Keys.hmacShaKeyFor(secret.toByteArray())
            return try {
                Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtStr)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun payload(secret: String, jwtStr: String): Map<String, Any>? {
            val secretKey: Key = Keys.hmacShaKeyFor(secret.toByteArray())
            return try {
                val claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtStr)
                    .body
                claims.toMap()
            } catch (e: Exception) {
                null
            }
        }
    }

    object json {

        internal var objectMapper: ObjectMapper = ObjectMapper()

        fun toString(obj: Any, defaultValue: String? = null): String {
            return try {
                objectMapper.writeValueAsString(obj)
            } catch (e: Exception) {
                defaultValue ?: ""
            }
        }
    }
}
