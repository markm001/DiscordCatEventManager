package com.ccat.catmanager.model.repository

import com.ccat.catmanager.model.entity.UserTimezoneEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserTimezoneDao: JpaRepository<UserTimezoneEntity, Long> {
}