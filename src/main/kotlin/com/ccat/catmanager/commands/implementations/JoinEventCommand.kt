package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.EventIdNotFoundException
import com.ccat.catmanager.model.EventParticipantRequest
import com.ccat.catmanager.model.entity.EventParticipantEntity
import com.ccat.catmanager.model.service.EventService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class JoinEventCommand(
    override val data: CommandData,

    private val eventService: EventService
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()
        try {
            val eventId: Long = event.getOption("eventid")!!.asLong
            val startFrame: LocalDateTime = LocalDateTime.parse(event.getOption("starttime")!!.asString)
            val endFrame: LocalDateTime = LocalDateTime.parse(event.getOption("endtime")!!.asString)

            event.guild?.getScheduledEventById(eventId)
                ?: throw EventIdNotFoundException("Event with Id:$eventId was not found.")

            val request = EventParticipantRequest(
                event.user.idLong,
                eventId,
                startFrame,
                endFrame
            )

            val response: EventParticipantEntity = eventService.createParticipantData(request)

            event.hook.sendMessage(
                "Request sent to join Event Id:" + response.eventId
                        + ". Available time from:" + response.startingTime + " to:"
                        + response.endingTime + " (in Server-time)"
            ).queue()

        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is IllegalStateException -> {
                    event.hook.sendMessage(
                        "An error occurred parsing the **Event-Id**. " + e.message
                                + ". Please check that you entered a valid Long value for the Id field."
                    )
                        .queue()
                }
                is IllegalArgumentException, is DateTimeParseException -> {
                    event.hook.sendMessage(
                        "An error occurred setting the **Date**. " + e.message
                                + ". Please check if your chosen dates are valid."
                    )
                        .queue()
                }
                is EventIdNotFoundException -> {
                    event.hook.sendMessage(
                        "An error occurred retrieving the requested **Event**" + e.message
                                + ". Please check that an Event with the Event-Id really exists."
                    )
                        .queue()
                }
                else -> throw e
            }
        }
    }
}