package com.team04.back.domain.weather.geo.dto

import com.team04.back.infra.weather.dto.GeoDirectResponse

data class GeoLocationDto(
    val name: String? = null,
    val country: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
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

