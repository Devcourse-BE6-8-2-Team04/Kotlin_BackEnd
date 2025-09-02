package com.team04.back.domain.member.member.dto

import com.team04.back.domain.member.member.entity.Gender
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.entity.Tendency
import java.time.LocalDateTime

class MemberWithUserIdDto(
    val id: Int?,
    val userId: String,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val email: String,
    val age: Int,
    val gender: Gender,
    val tendency: Tendency
) {
    constructor(member: Member) : this(
        member.id,
        member.userId,
        member.createDate,
        member.modifyDate,
        member.email,
        member.age,
        member.gender,
        member.tendency
    )
}