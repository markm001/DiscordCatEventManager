package com.ccat.catmanager.commands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class SimpleCommand(
    open val data: CommandData
) {
    open fun executeCommand(event: SlashCommandInteractionEvent) {}
}
