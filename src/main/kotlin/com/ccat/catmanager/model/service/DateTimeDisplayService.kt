package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.EventViewResponse
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.DateTimeFormatter

@Service
class DateTimeDisplayService(
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")
) {
    fun display(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime.format(dateTimeFormat) ?: zonedDateTime.toString()
    }

    fun display(localDateTime: LocalDateTime): String {
        return localDateTime.format(dateTimeFormat) ?: localDateTime.toString()
    }

    fun convertToZoneId(response: EventViewResponse, zoneId: ZoneId): EventViewResponse {
        return EventViewResponse(
            response.participantIds,
            response.excludedIds,
            response.suggestedStartTime.withZoneSameInstant(zoneId),
            response.suggestedEndTime.withZoneSameInstant(zoneId),
            response.earliestRequestedTime.withZoneSameInstant(zoneId),
            response.latestRequestedTime.withZoneSameInstant(zoneId)
        )
    }
}