package com.team04.back.domain.member.member.repository

import com.team04.back.domain.member.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Int> {
    fun findByUserId(username: String): Member?
    fun findByApiKey(apiKey: String): Member?
}
