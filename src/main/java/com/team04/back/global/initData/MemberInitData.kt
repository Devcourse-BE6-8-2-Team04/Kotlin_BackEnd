package com.team04.back.global.initData

import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
import com.team04.back.domain.member.member.repository.MemberRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

@Configuration
class MemberInitData(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Bean
    @Order(3)
    fun memberInitDataRunner(): ApplicationRunner {
        return ApplicationRunner { insertData() }
    }

    @Transactional
    fun insertData() {
        if (memberRepository.count() > 0) return

        val member1 = Member(
            userId = "user1",
            password = passwordEncoder.encode("password1"),
            email = "user1@example.com",
            age = 25,
            gender = Gender.MALE,
            tendency = Tendency.NEUTRAL
        )
        val member2 = Member(
            userId = "user2",
            password = passwordEncoder.encode("password2"),
            email = "user2@example.com",
            age = 30,
            gender = Gender.FEMALE,
            tendency = Tendency.COLD_SENSITIVE
        )

        memberRepository.saveAll(listOf(member1, member2))
    }
}
