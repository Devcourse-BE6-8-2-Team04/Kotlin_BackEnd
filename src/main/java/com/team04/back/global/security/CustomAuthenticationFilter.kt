package com.team04.back.global.security

import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.service.MemberService
import com.team04.back.global.exception.ServiceException
import com.team04.back.global.rq.Rq
import com.team04.back.standard.util.Ut
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    @Lazy private val memberService: MemberService,
    private val rq: Rq
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("Processing request for ${request.requestURI}")

        try {
            handleFilter(request, response, filterChain)
        } catch (e: ServiceException) {
            val rsData = e.rsData
            response.contentType = "application/json"
            response.status = rsData.statusCode
            response.writer.write(Ut.json.toString(rsData))
        }
    }

    private fun handleFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI

        // Swagger & 공개 API 패스
        val openApiPrefixes = listOf(
            "/swagger-ui",
            "/v3/api-docs",
            "/api/v1/geos",
            "/api/v1/weathers",
            "/api/v1/weathers/location",
//            "/api/v1/cloth",
//            "/api/v1/cloth/details",
            "/api/v1/auth/login",
            "/api/v1/auth/logout"
        )

        if (!uri.startsWith("/api/") || openApiPrefixes.any { uri.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        val (apiKey, accessToken) = run {
            val header = rq.header("Authorization", "")
            if (header.isNotBlank()) {
                if (!header.startsWith("Bearer ")) throw ServiceException(
                    "401-2", "Authorization 헤더가 Bearer 형식이 아닙니다."
                )
                val token = header.removePrefix("Bearer ").trim()
                rq.cookieValue("apiKey", "") to token
            } else {
                rq.cookieValue("apiKey", "") to rq.cookieValue("accessToken", "")
            }
        }


        val member: Member = when {
            accessToken.isNotBlank() -> {
                val payload = memberService.payload(accessToken)
                if (payload == null) {
                    if (apiKey.isNotBlank()) {
                        val memberFromApiKey = memberService.findByApiKey(apiKey)
                            ?: throw ServiceException("401-3", "API 키가 유효하지 않습니다.")
                        val newAccessToken = memberService.genAccessToken(memberFromApiKey)
                        val cookie = Cookie("accessToken", newAccessToken).apply {
                            path = "/"; isHttpOnly = true; maxAge = 3600
                        }
                        response.addCookie(cookie)
                        response.setHeader("Authorization", "Bearer $newAccessToken")
                        memberFromApiKey
                    } else {
                        throw ServiceException("401-4", "액세스 토큰이 유효하지 않습니다.")
                    }
                } else {
                    // ✅ 여기만 DB 재조회로 변경
                    val userId = payload["userId"] as? String
                        ?: throw ServiceException("401-5", "토큰에 userId가 없습니다.")
                    memberService.findByUsername(userId)
                        ?: throw ServiceException("404-1", "존재하지 않는 회원입니다.")
                }
            }

            apiKey.isNotBlank() -> memberService.findByApiKey(apiKey)
                ?: throw ServiceException("401-3", "API 키가 유효하지 않습니다.")

            else -> {
                filterChain.doFilter(request, response)
                return
            }
        }


        val user: UserDetails = SecurityUser(
            id = member.id,
            username = member.userId,
            password = member.password,
            name = member.userId,
            authorities = emptyList()
        )

        val auth: Authentication = UsernamePasswordAuthenticationToken(
            user,
            user.password,
            user.authorities
        )

        SecurityContextHolder.getContext().authentication = auth
        filterChain.doFilter(request, response)
    }
}
