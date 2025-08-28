package com.team04.back.standard.dto

import com.team04.back.standard.extensions.toCamelCase
import org.springframework.data.domain.Sort

enum class ReviewSearchSortType {
    ID,
    ID_ASC,
    EMAIL,
    EMAIL_ASC,
    CREATED_AT,
    CREATED_AT_ASC,
    LOCATION,
    LOCATION_ASC;

    val isAsc = name.endsWith("_ASC")

    val property = name.removeSuffix("_ASC").toCamelCase()

    val direction = if (isAsc) Sort.Direction.ASC else Sort.Direction.DESC

    val sortBy = Sort.by(direction, property)
}