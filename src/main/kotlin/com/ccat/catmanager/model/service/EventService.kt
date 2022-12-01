package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.EventCreateRequest
import com.ccat.catmanager.model.EventParticipantRequest
import com.ccat.catmanager.model.entity.EventParticipantEntity
import com.ccat.catmanager.model.entity.UserTimezoneEntity
import com.ccat.catmanager.model.repository.EventParticipantDao
import com.ccat.catmanager.model.repository.UserTimezoneDao
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.ScheduledEvent
import org.springframework.stereotype.Service
import java.time.*
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class EventService(
    val eventParticipantDao: EventParticipantDao,
    val userTimezoneDao: UserTimezoneDao
) {
    /**
     * Converts UserTimes to ZonedDateTime using set Timezone
     */
    fun createParticipantData(request: EventParticipantRequest): EventParticipantEntity {
        val userZoneId: ZoneId = setUserZoneId(request.userId)

        val zonedStartTime: ZonedDateTime = request.startingTime.atZone(userZoneId)
        val zonedEndTime: ZonedDateTime = request.endingTime.atZone(userZoneId)

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
     * Get User Zone-Id
     * Convert request.time to OffsetDateTime -> return createEvent as CompletableFuture to queue
     */
    fun createEventData(request: EventCreateRequest, guild: Guild): CompletableFuture<ScheduledEvent> {
        val userZoneId: ZoneId = setUserZoneId(request.creatorId)

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

    private fun setUserZoneId(userId: Long): ZoneId {
        val userTimezoneResponse: Optional<UserTimezoneEntity> = userTimezoneDao.findById(userId)

        val userZoneId: ZoneId = if (userTimezoneResponse.isPresent) {
            userTimezoneResponse.get().zoneId
        } else {
            ZoneId.systemDefault()
        }

        return userZoneId
    }

    fun getParticipantDataForEventId(eventId: Long): List<EventParticipantEntity>{
        return eventParticipantDao.findByEventId(eventId)
    }
}