package com.team04.back.domain.member.member.controller

import com.team04.back.domain.member.member.dto.MemberDto
import com.team04.back.domain.member.member.dto.MemberLoginResBody
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.service.MemberService
import com.team04.back.global.exception.ServiceException
import com.team04.back.global.rq.Rq
import com.team04.back.global.rsData.RsData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthController", description = "로그인/로그아웃 관련 API")
class AuthController(
    private val memberService: MemberService,
    private val rq: Rq
) {

    data class MemberLoginReqBody(
        @field:NotBlank @field:Size(min = 2, max = 30)
        val username: String,
        @field:NotBlank @field:Size(min = 2, max = 30)
        val password: String
    )

    @PostMapping("/login")
    @Transactional(readOnly = true)
    @Operation(summary = "로그인")
    fun login(@RequestBody @Valid reqBody: MemberLoginReqBody): RsData<MemberLoginResBody> {
        val member: Member = memberService.findByUsername(reqBody.username)
            ?: throw ServiceException("401-1", "존재하지 않는 아이디입니다.")

        memberService.checkPassword(member, reqBody.password)

        val accessToken = memberService.genAccessToken(member)

        rq.setCookie("apiKey", member.apiKey)
        rq.setCookie("accessToken", accessToken)

        return RsData(
            resultCode = "200-1",
            msg = "${member.userId}님 환영합니다.",
            data = MemberLoginResBody(
                accessToken = accessToken,
                apiKey = member.apiKey,
                memberDto = MemberDto(member)
            )
        )
    }

    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(): RsData<Void?> {
        rq.deleteCookie("apiKey")
        rq.deleteCookie("accessToken")
        return RsData(
            resultCode = "200-1",
            msg = "로그아웃 되었습니다.",
            data = null
        )
    }
}
