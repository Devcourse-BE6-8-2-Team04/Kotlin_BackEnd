package com.team04.back.domain.member.member.dto

import com.team04.back.domain.member.member.entity.Member
import java.time.LocalDateTime

class MemberWithUserIdDto(
    val id: Int?,
    val userId: String,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
) {
    constructor(member: Member) : this(
        member.id,
        member.userId,
        member.createDate,
        member.modifyDate,
    )
}