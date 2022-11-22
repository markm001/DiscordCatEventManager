package com.ccat.catmanager.commands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface ServerCommand {
    fun executeCommand(event: SlashCommandInteractionEvent)
}