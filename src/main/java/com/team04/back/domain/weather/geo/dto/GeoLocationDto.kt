package com.team04.back.domain.weather.geo.dto

import com.team04.back.infra.weather.dto.GeoDirectResponse
import jakarta.validation.constraints.NotNull

data class GeoLocationDto(
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val localName: String? = null
) {
    constructor(geoDirectResponse: GeoDirectResponse) : this(
        name = geoDirectResponse.name,
        country = geoDirectResponse.country,
        lat = geoDirectResponse.lat,
        lon = geoDirectResponse.lon,
        localName = geoDirectResponse.localNames?.korean
    )
}
