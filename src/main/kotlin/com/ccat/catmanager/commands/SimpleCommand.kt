package com.ccat.catmanager.commands

import net.dv8tion.jda.api.interactions.commands.build.CommandData

data class SimpleCommand(
    val instance: ServerCommand,
    val data: CommandData
)
