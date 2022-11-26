package com.ccat.catmanager.model.entity

import org.hibernate.Hibernate
import java.time.ZonedDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name="event_participants")
data class EventParticipantEntity (
    @Id val id: Long,
    val userId: Long,
    val eventId: Long,
    val startingTime: ZonedDateTime,
    val endingTime: ZonedDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as EventParticipantEntity

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , userId = $userId , eventId = $eventId , startingTime = $startingTime , endingTime = $endingTime )"
    }
}