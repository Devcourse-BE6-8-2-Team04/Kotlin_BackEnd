package com.team04.back.domain.history.history.repository

import com.team04.back.domain.history.history.entity.History
import org.springframework.data.jpa.repository.JpaRepository

interface HistoryRepository : JpaRepository<History, Long>{


}
