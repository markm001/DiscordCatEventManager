package com.ccat.catmanager.exceptions

class GuildInternalCommandException(
    override val message: String
): RuntimeException(message)

class EventIdNotFoundException(
    override val message: String
): RuntimeException(message)

class EventDataNotFoundException(
    override val message: String
): RuntimeException(message)

class InvalidDateException(
    override val message: String
): RuntimeException(message)