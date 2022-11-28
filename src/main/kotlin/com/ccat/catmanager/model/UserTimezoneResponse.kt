package com.ccat.catmanager.model

import java.time.ZoneId

data class UserTimezoneResponse (
    val userId: Long,
    val zoneId: ZoneId
)