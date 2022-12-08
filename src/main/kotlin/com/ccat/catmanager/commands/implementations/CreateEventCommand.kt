package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.GuildInternalCommandException
import com.ccat.catmanager.util.ResponseHandler
import com.ccat.catmanager.model.EventCreateRequest
import com.ccat.catmanager.model.service.EventService
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class CreateEventCommand(
    override val data: CommandData,
    private val eventService: EventService
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        val guild: Guild= event.guild ?: throw GuildInternalCommandException(" Used in ${event.channelType}")
        val defaultChannel: DefaultGuildChannelUnion = guild.defaultChannel!!

        try {
            val topic: String = event.getOption("topic")?.asString ?: "Event ${event.idLong}"
            val start: LocalDateTime = LocalDateTime.parse(event.getOption("starttime")?.asString)
            val end: LocalDateTime = LocalDateTime.parse(event.getOption("endtime")?.asString)

            val request = EventCreateRequest(
                event.user.idLong,
                topic,
                defaultChannel,
                start,
                end
            )

            eventService.createEventData(request, guild).whenComplete { scheduledEvent, error ->
                if (error != null) {
                    ResponseHandler.error(event, error.message ?: "Event could not be created. Please try again later.").queue()
                    return@whenComplete
                }

                ResponseHandler.success(
                    event, "Event created successfully",
                    "Event with Id:${scheduledEvent.idLong} has been created."
                ).queue()

                scheduledEvent.manager.setDescription(
                    "Event has been scheduled to join, please use the " +
                            "`/joinevent eventid:${scheduledEvent.idLong} [starttime] [endtime]` command to let the author know your times of availability."
                ).queue()
            }

        } catch (e: Exception) {
            when (e) {
                is GuildInternalCommandException -> {
                    ResponseHandler.externalAccessError(event, e.message)
                }
                is IllegalArgumentException, is DateTimeParseException -> {
                    ResponseHandler.error(event, e.message ?: "Please check if the chosen **Dates** are valid.").queue()
                }
                else -> throw e
            }
        }
    }
}