package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.model.service.EventViewService
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.ZoneId

class EventViewCommand(
    override val data: CommandData,
    private val eventViewService: EventViewService

): SimpleCommand(data){
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.getOption("eventid")
        event.getOption("zoneid") ?: ZoneId.systemDefault()

        eventViewService.dateTimeEvaluation(event.getOption("eventid")!!.asLong)
    }
}