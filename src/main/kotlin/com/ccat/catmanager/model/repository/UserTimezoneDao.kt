package com.ccat.catmanager.model.repository

import com.ccat.catmanager.model.entity.UserTimezoneEntity
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserTimezoneDao: JpaRepository<UserTimezoneEntity, Long> {
    @Cacheable("zoneIdForUsers")
    override fun findById(id: Long): Optional<UserTimezoneEntity>

    @CacheEvict("zoneIdForUsers", allEntries = true)
    override fun <S : UserTimezoneEntity> save(entity: S): S
}