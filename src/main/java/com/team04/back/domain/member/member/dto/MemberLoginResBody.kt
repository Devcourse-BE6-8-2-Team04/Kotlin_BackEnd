package com.team04.back.domain.member.member.dto

data class MemberLoginResBody(
    val accessToken: String,
    var apiKey: String,
    var memberDto: MemberDto
)