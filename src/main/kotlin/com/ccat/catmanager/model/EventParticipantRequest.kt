package com.ccat.catmanager.model

import java.time.LocalDateTime

data class EventParticipantRequest (
    val userId: Long,
    val eventId: Long,
    val startingTime: LocalDateTime,
    val endingTime: LocalDateTime
)