package com.team04.back.domain.cloth.cloth.repository

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ClothRepository : JpaRepository<ClothInfo, Int> {
    fun findByMinFeelsLikeLessThanEqualAndMaxFeelsLikeGreaterThanEqual(
        min: Double?,
        max: Double?
    ): List<ClothInfo>

    @Query("SELECT c FROM ClothInfo c WHERE :temperature BETWEEN c.minFeelsLike AND c.maxFeelsLike")
    fun findByTemperature(@Param("temperature") temperature: Double?): List<ClothInfo>
}
