package com.team04.back.domain.user.user.service

import com.team04.back.domain.user.user.entity.User
import com.team04.back.domain.user.user.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder // 빈 등록 필요!
) {
    fun register(email: String, password: String): User {
        require(!userRepository.existsByEmail(email)) { "이미 가입된 이메일입니다." }
        val encodedPassword = passwordEncoder.encode(password)
        return userRepository.save(User(email = email, password = encodedPassword))
    }
}