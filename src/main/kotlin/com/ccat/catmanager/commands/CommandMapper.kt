package com.ccat.catmanager.commands

import com.ccat.catmanager.commands.implementations.CreateEventCommand
import com.ccat.catmanager.commands.implementations.JoinEventCommand
import com.ccat.catmanager.commands.implementations.PingCommand
import com.ccat.catmanager.listeners.CommandEnum
import com.ccat.catmanager.model.service.EventService
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component

@Component
class CommandMapper (
    val eventService: EventService,

    val msgAppend: String = "Format: yyyy-MM-dd hh:mm | (default, if not set: GMT+1)",

    val eventCreateOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "topic", "Topic for your event").setRequired(true),
        OptionData(OptionType.STRING, "starttime", "Starting DateTime. $msgAppend").setAutoComplete(true).setRequired(true),
        OptionData(OptionType.STRING, "endtime", "Ending DateTime. $msgAppend").setAutoComplete(true).setRequired(true)),

    val eventJoinOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "eventid", "EventId or String-Name").setAutoComplete(true).setRequired(true),
        OptionData(OptionType.STRING, "starttime", "Starting DateTime. $msgAppend").setAutoComplete(true).setRequired(true),
        OptionData(OptionType.STRING, "endtime", "Ending DateTime. $msgAppend").setAutoComplete(true).setRequired(true)),

    val commandMap: Map<String, SimpleCommand> = mapOf(
        CommandEnum.PING.commandName to PingCommand(
            Commands.slash(CommandEnum.PING.commandName, "Send a test ping to yourself.")),
        CommandEnum.EVENTCREATE.commandName to CreateEventCommand(
            Commands.slash(CommandEnum.EVENTCREATE.commandName, "Create a new Event.")
                .addOptions(eventCreateOptions), eventService),
        CommandEnum.JOINEVENT.commandName to JoinEventCommand(
            Commands.slash(CommandEnum.JOINEVENT.commandName, "Suggest a time you would be willing to join")
                .addOptions(eventJoinOptions),eventService)
    )
) {
    fun getListCommandData() : List<CommandData> {
        return commandMap.map { it.value.data }
    }
}