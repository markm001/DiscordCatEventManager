package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.model.ManagedEventRequest
import com.ccat.catmanager.model.entity.ManagedEventEntity
import com.ccat.catmanager.model.service.ManagedEventService
import com.ccat.catmanager.util.ResponseHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.ErrorResponse

class ManageEventCommand(
    override val data: CommandData,
    private val managedEventService: ManagedEventService
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        try {
            val eventId: Long = event.getOption("eventid")!!.asLong

            event.guild?.retrieveScheduledEventById(eventId)?.queue({
                val response: ManagedEventEntity = managedEventService.addManagedEvent(
                    ManagedEventRequest(
                        it.idLong,
                        it.guild.idLong
                    )
                )

                ResponseHandler.success(
                    event.hook,
                    "Queued successfully",
                    "Event ${it.name} with Id:${response.eventId} has been added to the managing queue."
                ).queue()
            },
                ErrorHandler()
                    .handle(ErrorResponse.SCHEDULED_EVENT) {
                        ResponseHandler.error(
                            event.hook,
                            it.message ?: "Please check if the chosen **Dates** are valid."
                        ).queue()
                    }
            )

        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is IllegalStateException -> {
                    ResponseHandler.error(
                        event.hook,
                        e.message ?: "Please check if the **Event-Id** is valid."
                    ).queue()
                }
                else -> throw e
            }
        }

    }
}