package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.EventViewResponse
import org.springframework.stereotype.Service
import java.time.DateTimeException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class DateTimeDisplayService(
    val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")
) {
    fun convertToDisplay(zonedDateTime: ZonedDateTime): String {
        try {
            return zonedDateTime.format(dateTimeFormat)
        } catch (e: DateTimeException) {
            //TODO: LOG THIS!
            e.printStackTrace()
        }
        return zonedDateTime.toString()
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