package com.team04.back.global.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity{

    @CreatedDate
    @Column(updatable = false)
    open var createAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    open var modifyAt: LocalDateTime = LocalDateTime.now()
}