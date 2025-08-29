package com.team04.back.global.aspect.aop

import com.team04.back.domain.history.history.entity.History
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TestHistoryRepository : JpaRepository<History, Long> {

    // 테스트 용도: 특정 path에 해당하는 모든 히스토리 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM History h WHERE h.path = :path")
    fun deleteAllByPath(path: String)
}
