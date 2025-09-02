package com.team04.back.domain.user.user.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @JsonIgnore // 응답 serialization에서 password 제외
    @Column(nullable = false, length = 60)  // BCrypt 해시 길이(60) 고려 — 스키마 절약 및 명시성 향상
    val password: String,
)
