package com.team04.back.domain.user.user.service

import com.team04.back.domain.user.user.entity.User
import com.team04.back.domain.user.user.repository.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
@Transactional
    fun register(email: String, password: String): User {
        val normalizedEmail = email.trim().lowercase()
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.")
        }
        val encodedPassword = passwordEncoder.encode(password)
        try {
            return userRepository.save(User(email = normalizedEmail, password = encodedPassword))
        } catch (e: DataIntegrityViolationException) {
            // 유니크 제약 등 동시 상황 방어
            throw ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.")
        }
    }
}