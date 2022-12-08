package com.ccat.catmanager.exceptions

class GuildInternalCommandException(
    override val message: String
): RuntimeException(message)