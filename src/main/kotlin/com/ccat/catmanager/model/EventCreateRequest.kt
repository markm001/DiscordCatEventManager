package com.ccat.catmanager.model

import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion
import java.time.LocalDateTime

data class EventCreateRequest (
    val topic: String,
    val channel: DefaultGuildChannelUnion,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)