package com.team04.back.infra.cache

import com.team04.back.domain.weather.geo.dto.GeoLocationDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class GeoCache(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val cacheKeyPrefix = "geo:search:"
    private val normalizedKeyPrefix = "geo:normalized:"

    private val geoLocationTtl: Duration = Duration.ofDays(7)
    private val normalizedCityTtl: Duration = Duration.ofDays(7)

    fun getGeoLocations(location: String): List<GeoLocationDto>? {
        val key = cacheKeyPrefix + location
        @Suppress("UNCHECKED_CAST")
        val cached = redisTemplate.opsForValue().get(key) as? List<GeoLocationDto>
        if (cached != null) {
            redisTemplate.expire(key, geoLocationTtl)
        }
        return cached
    }


    fun putGeoLocations(location: String, data: List<GeoLocationDto>) {
        val key = cacheKeyPrefix + location
        redisTemplate.opsForValue().set(key, data, geoLocationTtl)
    }

    fun getNormalizedCity(rawCityName: String): String? {
        val key = normalizedKeyPrefix + rawCityName
        val cached = redisTemplate.opsForValue().get(key) as? String
        if (cached != null) {
            redisTemplate.expire(key, normalizedCityTtl)
        }
        return cached
    }

    fun putNormalizedCity(rawCityName: String, normalized: String) {
        val key = normalizedKeyPrefix + rawCityName
        redisTemplate.opsForValue().set(key, normalized, normalizedCityTtl)
    }
}
