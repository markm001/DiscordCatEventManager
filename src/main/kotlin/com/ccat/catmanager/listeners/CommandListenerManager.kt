package com.ccat.catmanager.listeners

import com.ccat.catmanager.commands.CommandMapper
import com.ccat.catmanager.commands.SimpleCommand
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class CommandListenerManager (
    val commandMapper: CommandMapper,
    val commandMap: Map<String, SimpleCommand> = commandMapper.commandMap
): ListenerAdapter() {

    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.updateCommands().addCommands(commandMapper.getListCommandData()).queue()
    }


    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        try {
            val command: SimpleCommand = commandMap[event.name] ?: throw NoSuchElementException()
            command.executeCommand(event)
        } catch (e: NoSuchElementException) { //TODO: Log this - Shouldn't happen, error in guild.CommandData
            e.printStackTrace()
        }
    }
}