package com.team04.back.domain.user.user.entity
import com.fasterxml.jackson.annotation.JsonIgnore
import com.team04.back.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table


@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true, length = 100)
    val email: String,

    @JsonIgnore // 응답 serialization에서 password 제외
    @Column(nullable = false, length = 60)
    val password: String,

    // Java 코드에서 dummy 필드 추가
    @Column(nullable = true)
    var dummy: Long? = null

) : BaseEntity() {
    // Java getter/setter 유지
    fun setDummy(dummy: Long?) {
        this.dummy = dummy
    }

    fun getDummy(): Long? {
        return this.dummy
    }
}
