package com.team04.back.domain.cloth.cloth.repository;

import com.team04.back.domain.cloth.cloth.entity.ClothInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothRepository extends JpaRepository<ClothInfo, Integer> {
    List<ClothInfo> findByMinFeelsLikeLessThanEqualAndMaxFeelsLikeGreaterThanEqual(Double min, Double max);


    @Query("SELECT c FROM ClothInfo c WHERE :temperature BETWEEN c.minFeelsLike AND c.maxFeelsLike")
    List<ClothInfo> findByTemperature(@Param("temperature") Double temperature);
}
