package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.InvalidDateException
import com.ccat.catmanager.model.EventParticipantRequest
import com.ccat.catmanager.model.entity.EventParticipantEntity
import com.ccat.catmanager.model.service.DateTimeDisplayService
import com.ccat.catmanager.model.service.EventService
import com.ccat.catmanager.util.ResponseHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.ErrorResponse
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class JoinEventCommand(
    override val data: CommandData,

    private val eventService: EventService,
    private val displayService: DateTimeDisplayService
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()
        try {
            val eventId: Long = event.getOption("eventid")!!.asLong

            val startFrame: LocalDateTime = LocalDateTime.parse(event.getOption("starttime")?.asString)
            val endFrame: LocalDateTime = LocalDateTime.parse(event.getOption("endtime")?.asString)

            val startDisplay: String = displayService.display(startFrame)
            val endDisplay: String = displayService.display(endFrame)

            if (startFrame.isAfter(endFrame) || startFrame.isEqual(endFrame)) {
                throw InvalidDateException(
                    "Cannot have an end-time before start-time.\n" +
                            "$startDisplay - $endDisplay"
                )
            }

            event.guild?.retrieveScheduledEventById(eventId)?.queue(
                {
                    val request = EventParticipantRequest(
                        event.user.idLong,
                        eventId,
                        startFrame,
                        endFrame
                    )

                    val response: EventParticipantEntity = eventService.createParticipantData(request)

                    ResponseHandler.success(
                        event.hook,
                        "Request to join ${it.name} id:[${response.eventId}] successful",
                        "Available time, \nfrom: $startDisplay" +
                                "\nto: $endDisplay \n*`(times converted to Server time)`*"
                    ).queue()
                },
                ErrorHandler().handle(ErrorResponse.SCHEDULED_EVENT) {
                    ResponseHandler.error(
                        event.hook,
                        it.message ?: "Please check if the chosen **Dates** are valid."
                    ).queue()
                }
            )

        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is IllegalStateException -> {
                    ResponseHandler.error(event.hook, "${e.message} Please check if the **Event-Id** is valid.")
                        .queue()
                }
                is DateTimeParseException, is InvalidDateException -> {
                    ResponseHandler.error(event.hook, "${e.message} Please check if the chosen **Dates** are valid.")
                        .queue()
                }
                else -> throw e
            }
        }
    }
}