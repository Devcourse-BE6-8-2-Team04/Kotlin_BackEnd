package com.team04.back.domain.member.member.controller

import com.team04.back.domain.member.member.dto.MemberWithUserIdDto
import com.team04.back.domain.member.member.entity.Member
import com.team04.back.domain.member.member.service.MemberService
import com.team04.back.global.exception.ServiceException
import com.team04.back.global.rq.Rq
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "MemberController", description = "회원 관련 API")
@SecurityRequirement(name = "bearerAuth")
class MemberController(
    private val memberService: MemberService,
    private val rq: Rq
) {

    data class MemberJoinReqBody(
        @field:NotBlank @field:Size(min = 2, max = 30)
        val userId: String,
        @field:NotBlank @field:Size(min = 2, max = 30)
        val password: String,
        @field:NotBlank @field:Size(min = 2, max = 30)
        val email: String
    )

//    @PostMapping
//    @Transactional
//    fun join(@RequestBody @Valid reqBody: MemberJoinReqBody): RsData<MemberDto> {
//        val member = memberService.join(reqBody.userId, reqBody.password, reqBody.email)
//        return RsData(
//            resultCode = "201-1",
//            msg = "${member.userId}님 환영합니다. 회원가입이 완료되었습니다.",
//            data = MemberDto(member)
//        )
//    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    @Operation(summary = "내 정보 조회")
    fun me(): MemberWithUserIdDto {
        val actor: Member = memberService.findById(rq.actor?.id
            ?: throw ServiceException("401-1", "로그인이 필요합니다."))
            ?: throw ServiceException("404-1", "존재하지 않는 회원입니다.")

        return MemberWithUserIdDto(actor)
    }
}
