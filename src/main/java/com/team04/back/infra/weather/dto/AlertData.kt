package com.team04.back.infra.weather.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AlertData(
    @JsonProperty("sender_name")
    val senderName: String? = null,
    val event: String? = null,
    val start: Long? = null,
    val end: Long? = null,
    val description: String? = null,
    val tags: List<String>? = null
)
