package com.ccat.catmanager.exceptions

class EventIdNotFoundException(
    override val message: String
): RuntimeException(message)