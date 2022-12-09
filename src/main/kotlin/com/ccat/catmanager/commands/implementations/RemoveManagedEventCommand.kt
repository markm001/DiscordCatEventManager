package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.EventIdNotFoundException
import com.ccat.catmanager.exceptions.GuildInternalCommandException
import com.ccat.catmanager.model.ManagedEventRequest
import com.ccat.catmanager.model.service.ManagedEventService
import com.ccat.catmanager.util.ResponseHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class RemoveManagedEventCommand(
    override val data: CommandData,
    private val managedEventService: ManagedEventService
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        try {
            val guild: Guild = event.guild ?: throw GuildInternalCommandException(" Used in ${event.channelType}")

            val eventId: Long = event.getOption("eventid")!!.asLong

            val affectedRows: Int = managedEventService.removeManagedEvent(
                ManagedEventRequest(
                    eventId,
                    guild.idLong
                )
            )

            if(affectedRows > 0) {
                ResponseHandler.success(
                    event.hook,
                    "Event removed successfully",
                    "Event Id:$eventId has been removed from the management queue."
                ).queue()
            } else { throw EventIdNotFoundException("Event-Id:$eventId was not found in the management queue.") }

        } catch (e: Exception) {
            when (e) {
                is GuildInternalCommandException -> {
                    ResponseHandler.externalAccessError(event, e.message)
                }
                is NumberFormatException, is IllegalStateException -> {
                    ResponseHandler.error(
                        event.hook,
                        e.message ?: "Please check if you entered a valid Long value for the Id field."
                    ).queue()
                }
                is EventIdNotFoundException -> {
                    ResponseHandler.error(
                        event.hook,
                        e.message
                    ).queue()
                }
                else -> throw e
            }
        }

    }
}