package com.team04.back.domain.member.member.dto

import com.team04.back.domain.member.member.entity.Member
import java.time.LocalDateTime

data class MemberDto(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val userId: String
) {
    constructor(member: Member) : this(
        member.id ?: 0,
        member.createDate,
        member.modifyDate,
        member.userId
    )
}
