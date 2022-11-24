package com.ccat.catmanager.commands

import com.ccat.catmanager.commands.implementations.CreateEventCommand
import com.ccat.catmanager.commands.implementations.PingCommand
import com.ccat.catmanager.listeners.CommandEnum
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component

@Component
class CommandMapper (
    val eventCreateOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "topic", "Add a topic for your event").setRequired(true),
        OptionData(OptionType.STRING, "starttime", "Add a starting date").setAutoComplete(true).setRequired(true),
        OptionData(OptionType.STRING, "endtime", "Add an ending date").setAutoComplete(true).setRequired(true)),

    val commandMap: Map<String, SimpleCommand> = mapOf(
        CommandEnum.PING.commandName to PingCommand(
            Commands.slash(CommandEnum.PING.commandName, "Send a test ping to yourself.")),
        CommandEnum.EVENTCREATE.commandName to CreateEventCommand(
            Commands.slash(CommandEnum.EVENTCREATE.commandName, "Create a new Event.")
                .addOptions(eventCreateOptions))
    )
) {
    fun getListCommandData() : List<CommandData> {
        return commandMap.map { it.value.data }
    }
}