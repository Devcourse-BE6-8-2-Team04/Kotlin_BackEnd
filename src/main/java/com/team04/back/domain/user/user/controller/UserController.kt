package com.team04.back.domain.user.user.controller

import com.team04.back.domain.user.user.service.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    data class RegisterRequest(
        @field:Email @field:NotBlank val email: String,
        @field:NotBlank @field:Size(min = 8, max = 72) val password: String,
    )

    @PostMapping
    fun register(@RequestBody @Valid req: RegisterRequest) =
        userService.register(req.email, req.password)
}