package com.ccat.catmanager.model.entity

import org.hibernate.Hibernate
import java.time.ZoneId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="usertimezones")
data class UserTimezoneEntity (
    @Id val userId: Long,
    val zoneId: ZoneId
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserTimezoneEntity

        return userId == other.userId
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(userId = $userId )"
    }
}