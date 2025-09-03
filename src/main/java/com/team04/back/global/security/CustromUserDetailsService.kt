package com.team04.back.global.security

import com.team04.back.domain.member.member.service.MemberService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val memberService: MemberService
) : UserDetailsService {

    override fun loadUserByUsername(userId: String): UserDetails {
        val member = memberService.findByUsername(userId)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")

        return SecurityUser(
            id = member.id ?: 0,
            username = member.userId,
            password = member.password,
            name = member.userId,
            authorities = emptyList()
        )
    }
}
