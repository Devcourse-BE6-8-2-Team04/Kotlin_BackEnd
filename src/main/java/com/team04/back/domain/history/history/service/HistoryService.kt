package com.team04.back.domain.history.history.service

import com.team04.back.domain.history.history.entity.History
import com.team04.back.domain.history.history.repository.HistoryRepository
import com.team04.back.domain.weather.geo.service.GeoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class HistoryService (
    private val historyRepository: HistoryRepository,
    private val geoService: GeoService
){
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveHistory(
        path: String,
        userId: String? = null,
        ipAddress: String = "127.0.0.1",
        latitude: Double? = null,
        longitude: Double? = null
    ) {

        val address = if (latitude != null && longitude != null)
            geoService.getLocationFromCoordinates(latitude, longitude)
         else
            null


        val history = History(
            path = path,
            userId = userId,
            ipAddress = ipAddress,
            address = address
        )
        historyRepository.save(history)
    }

}

