package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.ManagedEventRequest
import com.ccat.catmanager.model.entity.ManagedEventEntity
import com.ccat.catmanager.model.repository.ManagedEventsDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    @Transactional
    fun removeManagedEvent(request: ManagedEventRequest):Int {
        return managedEventsDao.deleteEventByIdAndGuild(
            request.eventId,
            request.guildId
        )
    }

    fun findAllManagedEvents(): List<ManagedEventEntity> {
        return managedEventsDao.findAll()
    }
}