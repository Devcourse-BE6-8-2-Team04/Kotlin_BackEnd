package com.team04.back.global.security

import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
import com.team04.back.domain.member.member.service.MemberService
import com.team04.back.global.exception.ServiceException
import com.team04.back.global.rq.Rq
import com.team04.back.standard.util.Ut
import jakarta.servlet.FilterChain
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
        // API 요청 아닌 경우 패스
        if (!request.requestURI.startsWith("/api/")) {
            filterChain.doFilter(request, response)
            return
        }

        // 인증 필요 없는 API
        val openApis = setOf(
            "/api/v1/members/login",
            "/api/v1/members/logout",
            "/api/v1/members/join"
        )
        if (request.requestURI in openApis) {
            filterChain.doFilter(request, response)
            return
        }

        // Authorization 헤더 또는 쿠키 가져오기
        val (apiKey, accessToken) = run {
            val header = rq.header("Authorization", "")
            if (header.isNotBlank()) {
                if (!header.startsWith("Bearer ")) throw ServiceException(
                    "401-2", "Authorization 헤더가 Bearer 형식이 아닙니다."
                )
                val parts = header.split(" ", limit = 3)
                parts[1] to if (parts.size == 3) parts[2] else ""
            } else {
                rq.cookieValue("apiKey", "") to rq.cookieValue("accessToken", "")
            }
        }

        val member: Member = when {
            accessToken.isNotBlank() -> {
                val payload = memberService.payload(accessToken)
                    ?: throw ServiceException("401-4", "액세스 토큰이 유효하지 않습니다.")

                Member(
                    userId = payload["userId"] as? String ?: "",
                    password = "",
                    email = payload["email"] as? String ?: "",
                    age = 0,
                    gender = Gender.MALE,
                    tendency = Tendency.NEUTRAL,
                    apiKey = apiKey
                )
            }

            apiKey.isNotBlank() -> memberService.findByApiKey(apiKey)
                ?: throw ServiceException("401-3", "API 키가 유효하지 않습니다.")

            else -> {
                filterChain.doFilter(request, response)
                return
            }
        }

        val user: UserDetails = SecurityUser(
            id = member.id!!,
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
