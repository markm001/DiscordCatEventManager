package com.ccat.catmanager.model.repository

import com.ccat.catmanager.model.entity.EventParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EventParticipantDao: JpaRepository<EventParticipantEntity, Long> {

}