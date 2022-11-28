package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.EventCreateRequest
import com.ccat.catmanager.model.EventParticipantRequest
import com.ccat.catmanager.model.entity.EventParticipantEntity
import com.ccat.catmanager.model.repository.EventParticipantDao
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ScheduledEvent
import org.springframework.stereotype.Service
import java.time.*
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class EventService(
    val eventParticipantDao: EventParticipantDao
) {
    //TODO: MOVE TO CONFIG!
    private val serverZoneId = ZoneId.systemDefault()

    /**
     * Converts UserTimes to ZonedDateTime using set Timezone
     */
    fun createParticipantData(request: EventParticipantRequest): EventParticipantEntity {
        val zonedStartTime: ZonedDateTime = request.startingTime.atZone(serverZoneId)
        val zonedEndTime: ZonedDateTime = request.endingTime.atZone(serverZoneId)

        return eventParticipantDao.save(
            EventParticipantEntity(
                UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                request.userId,
                request.eventId,
                zonedStartTime,
                zonedEndTime
            )
        )
    }

    /**
     * Convert request.time to OffsetDateTime -> return createEvent as CompletableFuture to queue
     */
    fun createEventData(request: EventCreateRequest, guild: Guild): CompletableFuture<ScheduledEvent> {
        //TODO: Call Timezone Service for User Entity here!
        val userZoneId: ZoneId = ZoneId.systemDefault()
        val userZoneOffset: ZoneOffset = userZoneId.rules.getOffset(Instant.now())

        val startOffsetTime: OffsetDateTime = OffsetDateTime.of(request.startTime, userZoneOffset)
        val endOffsetTime: OffsetDateTime = OffsetDateTime.of(request.endTime, userZoneOffset)

        return guild.createScheduledEvent(
            request.topic,
            request.channel.asMention,
            startOffsetTime,
            endOffsetTime
        ).submit()
    }
}