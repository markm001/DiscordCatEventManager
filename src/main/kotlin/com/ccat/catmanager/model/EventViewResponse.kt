package com.ccat.catmanager.model

import java.time.ZonedDateTime

data class EventViewResponse (
    val participantIds: Set<Long>,
    val excludedIds: Set<Long>,
    val suggestedStartTime: ZonedDateTime,
    val suggestedEndTime: ZonedDateTime,
    val earliestRequestedTime: ZonedDateTime,
    val latestRequestedTime: ZonedDateTime
)