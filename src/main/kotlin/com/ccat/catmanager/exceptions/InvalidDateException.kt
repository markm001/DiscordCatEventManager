package com.ccat.catmanager.exceptions

class InvalidDateException(
    override val message: String
): RuntimeException(message)