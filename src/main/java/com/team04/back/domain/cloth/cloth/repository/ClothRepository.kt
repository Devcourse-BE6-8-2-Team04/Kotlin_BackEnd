package com.team04.back.domain.cloth.cloth.repository

import com.team04.back.domain.cloth.cloth.entity.ClothInfo
import com.team04.back.domain.cloth.cloth.enums.ClothName
import com.team04.back.domain.cloth.cloth.enums.Style
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ClothRepository : JpaRepository<ClothInfo, Int> {

    @Query("SELECT c FROM ClothInfo c WHERE :temperature BETWEEN c.minFeelsLike AND c.maxFeelsLike")
    fun findByTemperature(@Param("temperature") temperature: Double?): List<ClothInfo>

    fun findFirstByClothNameAndStyle(clothName: ClothName, style: Style?) : ClothInfo?

    fun findByClothName(clothName: ClothName) : List<ClothInfo>
}
