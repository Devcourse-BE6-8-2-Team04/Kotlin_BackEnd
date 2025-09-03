package com.team04.back.global.rq

import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
import com.team04.back.global.security.SecurityUser
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class Rq(
    req: HttpServletRequest,
    resp: HttpServletResponse
) {

    private val request: HttpServletRequest = req
    private val response: HttpServletResponse = resp

    val actor: Member?
        get() {
            val principal = SecurityContextHolder.getContext().authentication?.principal
            if (principal !is SecurityUser) return null
            val user = principal as SecurityUser
            return Member(
                userId = user.username ?: "",
                password = "",
                email = "",
                age = 0,
                gender = Gender.MALE,
                tendency = Tendency.NEUTRAL,
                apiKey = ""
            ).apply { id = user.id }
        }


    fun header(name: String, defaultValue: String = ""): String =
        request.getHeader(name)?.takeIf { it.isNotBlank() } ?: defaultValue

    fun setHeader(name: String, value: String?) {
        val v = value.orEmpty()
        if (v.isBlank()) request.removeAttribute(name)
        else response.setHeader(name, v)
    }

    fun cookieValue(name: String, defaultValue: String = ""): String {
        return request.cookies
            ?.firstOrNull { it.name == name && it.value?.isNotBlank() == true }
            ?.value ?: defaultValue
    }

    fun setCookie(name: String, value: String?) {
        val v = value.orEmpty()
        val cookie = Cookie(name, v).apply {
            path = "/"
            isHttpOnly = true
            domain = "localhost"
            secure = true
            setAttribute("SameSite", "Strict")
            maxAge = if (v.isBlank()) 0 else 60 * 60 * 24 * 365
        }
        response.addCookie(cookie)
    }

    fun deleteCookie(name: String) {
        setCookie(name, null)
    }
}
