package com.team04.back.domain.member.member.entity

import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.util.*

@Entity
class Member(
    @Column(nullable = false, unique = true)
    var userId: String,   // 로그인 ID

    @Column(nullable = false)
    var password: String, // 로그인 비밀번호

    @Column(nullable = false)
    var email: String,    // 이메일

    @Column(nullable = false)
    var age: Int,        // 나이

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var gender: Gender,  // 성별

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var tendency: Tendency, // 추위 타는 정도

    @Column(nullable = false)
    var apiKey: String = UUID.randomUUID().toString()


) : BaseEntity() {

    fun modifyApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

}