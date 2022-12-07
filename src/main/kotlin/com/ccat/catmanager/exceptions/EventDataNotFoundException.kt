package com.ccat.catmanager.exceptions

class EventDataNotFoundException(
    override val message: String
): RuntimeException(message)