package com.team04.back.domain.history.history.entity

import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "history")
class History(
    var path: String,             // 접근 API 경로 (예: "/review")
    var userId: String? = null,   // 로그인한 사용자 ID (익명이면 null)
    var ipAddress: String,        // 사용자 IP
    var address: String? = null,  // 사용자 주소 (위도, 경도 기준 도시명)
) : BaseEntity()

