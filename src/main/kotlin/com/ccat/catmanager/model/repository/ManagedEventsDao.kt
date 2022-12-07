package com.ccat.catmanager.model.repository

import com.ccat.catmanager.model.entity.ManagedEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ManagedEventsDao : JpaRepository<ManagedEventEntity, Long> {
    @Modifying
    @Query("DELETE FROM ManagedEventEntity e WHERE eventId =:eventId AND guildId=:guildId")
    fun deleteEventByIdAndGuild(eventId: Long, guildId: Long): Int
}