package com.ccat.catmanager.commands

import com.ccat.catmanager.commands.implementations.*
import com.ccat.catmanager.listeners.CommandEnum
import com.ccat.catmanager.model.repository.UserTimezoneDao
import com.ccat.catmanager.model.service.DateTimeDisplayService
import com.ccat.catmanager.model.service.EventService
import com.ccat.catmanager.model.service.EventViewService
import com.ccat.catmanager.model.service.ManagedEventService
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component

@Component
class CommandMapper(
    val eventService: EventService,
    val eventViewService: EventViewService,
    val userTimezoneDao: UserTimezoneDao,
    val dateTimeDisplayService: DateTimeDisplayService,
    val manageEventService: ManagedEventService,

    val msgAppend: String = "Format: yyyy-MM-dd hh:mm | (default, if not set: GMT+1)",

    val eventCreateOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "topic", "Topic for your event").setRequired(true),
        OptionData(OptionType.STRING, "starttime", "Starting DateTime. $msgAppend").setAutoComplete(true)
            .setRequired(true),
        OptionData(OptionType.STRING, "endtime", "Ending DateTime. $msgAppend").setAutoComplete(true).setRequired(true)
    ),

    val eventJoinOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "eventid", "EventId or String-Name").setAutoComplete(true).setRequired(true),
        OptionData(OptionType.STRING, "starttime", "Starting DateTime. $msgAppend").setAutoComplete(true)
            .setRequired(true),
        OptionData(OptionType.STRING, "endtime", "Ending DateTime. $msgAppend").setAutoComplete(true).setRequired(true)
    ),
    val eventViewOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "eventid", "EventId or String-Name").setAutoComplete(true).setRequired(true),
        OptionData(OptionType.STRING, "zoneid", "Enter a Zone-Id, such as: **Africa/Cairo** to covert Event time data into").setAutoComplete(true).setRequired(false)
    ),
    val manageEventOptions: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "eventid", "EventId or String-Name").setAutoComplete(true).setRequired(true)
    ),

    val timesetOptions: OptionData = OptionData(OptionType.STRING, "zoneid", "Enter a Zone-Id, such as: **Asia/Tokyo**").setAutoComplete(true).setRequired(true),

    val commandMap: Map<String, SimpleCommand> = mapOf(
        CommandEnum.PING.commandName to PingCommand(
            Commands.slash(CommandEnum.PING.commandName, "Send a test ping to yourself.")
        ),
        CommandEnum.EVENTCREATE.commandName to CreateEventCommand(
            Commands.slash(CommandEnum.EVENTCREATE.commandName, "Create a new Event.")
                .addOptions(eventCreateOptions), eventService
        ),
        CommandEnum.JOINEVENT.commandName to JoinEventCommand(
            Commands.slash(CommandEnum.JOINEVENT.commandName, "Suggest a time you would be willing to join")
                .addOptions(eventJoinOptions), eventService
        ),
        CommandEnum.TIMESET.commandName to SetTimezoneCommand(
            Commands.slash(CommandEnum.TIMESET.commandName, "Set your current Timezone")
                .addOptions(timesetOptions), userTimezoneDao
        ),
        CommandEnum.EVENTVIEW.commandName to EventViewCommand(
            Commands.slash(CommandEnum.EVENTVIEW.commandName, "View user times for an Event")
                .addOptions(eventViewOptions), eventViewService, dateTimeDisplayService
        ),
        CommandEnum.MANAGE.commandName to ManageEventCommand(
            Commands.slash(CommandEnum.MANAGE.commandName, "Set an Event to be managed by the Bot")
                .addOptions(manageEventOptions), manageEventService
        ),
        CommandEnum.REMOVE.commandName to RemoveManagedEventCommand(
            Commands.slash(CommandEnum.REMOVE.commandName, "Remove Event from the Bot management queue")
                .addOptions(manageEventOptions), manageEventService
        )
    )
) {
    fun getListCommandData(): List<CommandData> {
        return commandMap.map { it.value.data }
    }
}