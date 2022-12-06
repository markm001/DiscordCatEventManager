package com.ccat.catmanager.model.repository

import com.ccat.catmanager.model.entity.ManagedEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ManagedEventsDao : JpaRepository<ManagedEventEntity, Long> {
    @Query("SELECT e FROM ManagedEventEntity e WHERE eventId=:eventId AND guildId=:guildId")
    fun findEventByIdAndGuildId(eventId: Long, guildId: Long): ManagedEventEntity
}