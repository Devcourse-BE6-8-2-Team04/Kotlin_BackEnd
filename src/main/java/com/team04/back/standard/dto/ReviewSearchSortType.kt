package com.team04.back.standard.dto

import com.team04.back.standard.extensions.toCamelCase
import org.springframework.data.domain.Sort

enum class ReviewSearchSortType {
    ID,         // 작성 날짜 최신순
    ID_ASC,
    EMAIL,
    EMAIL_ASC,
    DATE,       // 여행 날짜 최신순
    DATE_ASC,
    LOCATION,
    LOCATION_ASC;

    val isAsc = name.endsWith("_ASC")

    val property = name.removeSuffix("_ASC").toCamelCase()

    val direction = if (isAsc) Sort.Direction.ASC else Sort.Direction.DESC

    val sortBy = Sort.by(direction, property)
}