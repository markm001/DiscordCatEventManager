package com.ccat.catmanager.model.repository

import com.ccat.catmanager.model.entity.EventParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface EventParticipantDao: JpaRepository<EventParticipantEntity, Long> {

    @Query("SELECT e FROM EventParticipantEntity e WHERE eventId=:eventId")
    fun findByEventId(eventId: Long): List<EventParticipantEntity>
}