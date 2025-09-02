package com.team04.back.domain.user.user.controller

import com.team04.back.domain.user.user.service.UserService
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    data class RegisterRequest(
        @field:Email @field:NotBlank val email: String,
        @field:NotBlank val password: String,
        @field:NotBlank val nickname: String
    )

    @PostMapping
    fun register(@RequestBody @Valid req: RegisterRequest) =
        userService.register(req.email, req.password, req.nickname)
}