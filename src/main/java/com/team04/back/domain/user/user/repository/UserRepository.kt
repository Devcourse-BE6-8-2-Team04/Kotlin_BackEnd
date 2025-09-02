package com.team04.back.domain.user.user.repository

import com.team04.back.domain.user.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {
    fun existsByEmailIgnoreCase(email: String): Boolean
}