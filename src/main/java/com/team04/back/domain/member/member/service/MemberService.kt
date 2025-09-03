package com.team04.back.domain.member.member.service

import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
import com.team04.back.domain.member.member.repository.MemberRepository
import com.team04.back.global.exception.ServiceException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val authTokenService: AuthTokenService,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun count(): Long = memberRepository.count()

    fun join(userId: String, password: String, email: String, age: Int, gender: Gender, tendency: Tendency): Member {
        memberRepository.findByUserId(userId)?.let {
            throw ServiceException("409-1", "이미 존재하는 아이디입니다.")
        }

        val encodedPassword = passwordEncoder.encode(password)

        val member = Member(
            userId = userId,
            password = encodedPassword,
            email = email,
            age = age,
            gender = gender,
            tendency = tendency
        )

        return memberRepository.save(member)
    }

    fun findByUsername(userId: String): Member? =
        memberRepository.findByUserId(userId)

    fun findByApiKey(apiKey: String): Member? =
        memberRepository.findByApiKey(apiKey)

    fun genAccessToken(member: Member): String =
        authTokenService.genAccessToken(member)

    fun payload(accessToken: String): Map<String, Any>? =
        authTokenService.payload(accessToken)

    fun findById(id: Int): Member? =
        memberRepository.findById(id)
            .orElseThrow()

    fun findAll(): List<Member> =
        memberRepository.findAll()

    fun checkPassword(member: Member, password: String) {
        if (!passwordEncoder.matches(password, member.password)) {
            throw ServiceException("401-1", "비밀번호가 일치하지 않습니다.")
        }
    }
}
