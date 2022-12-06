package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.EventIdNotFoundException
import com.ccat.catmanager.model.ManagedEventRequest
import com.ccat.catmanager.model.entity.ManagedEventEntity
import com.ccat.catmanager.model.service.ManagedEventService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class RemoveManagedEventCommand(
    override val data: CommandData,
    private val managedEventService: ManagedEventService
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        try {
            val idOption = event.getOption("eventid")
            val eventId: Long = (idOption?.asLong)
                ?: throw NumberFormatException("${idOption?.asString} is not a valid Long.")

            val response: ManagedEventEntity = managedEventService.removeManagedEvent(
                ManagedEventRequest(
                    eventId,
                    event.guild!!.idLong
                )
            )

            event.hook.sendMessage(
                "Event Id:${response.eventId} has been removed from the management queue."
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