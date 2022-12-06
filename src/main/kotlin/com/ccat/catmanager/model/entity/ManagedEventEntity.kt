package com.ccat.catmanager.model.entity

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name="managedevents")
data class ManagedEventEntity (
    @Id
    val id: Long,
    val eventId: Long,
    val guildId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ManagedEventEntity

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , eventId = $eventId , guildId = $guildId )"
    }
}