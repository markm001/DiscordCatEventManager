package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.ManagedEventRequest
import com.ccat.catmanager.model.entity.ManagedEventEntity
import com.ccat.catmanager.model.repository.ManagedEventsDao
import org.springframework.stereotype.Service
import java.util.*

@Service
class ManagedEventService(
    private val managedEventsDao: ManagedEventsDao
) {
    fun addManagedEvent(request: ManagedEventRequest): ManagedEventEntity {
        return managedEventsDao.save(
            ManagedEventEntity(
                UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                request.eventId,
                request.guildId
            )
        )
    }

    fun removeManagedEvent(request: ManagedEventRequest): ManagedEventEntity {
        val response = findManagedEntity(request)

        managedEventsDao.deleteById(response.id)
        return response
    }

    private fun findManagedEntity(request: ManagedEventRequest) =
        managedEventsDao.findEventByIdAndGuildId(
            request.eventId,
            request.guildId
        )
}