package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
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
        val guild: Guild = event.guild!!
        val defaultChannel: DefaultGuildChannelUnion = guild.defaultChannel!!

        try {
            val topic: String = event.getOption("topic")!!.asString
            val start: LocalDateTime = LocalDateTime
                .parse(event.getOption("starttime")!!.asString)
            val end: LocalDateTime = LocalDateTime
                .parse(event.getOption("endtime")!!.asString)

            val request = EventCreateRequest(
                event.user.idLong,
                topic,
                defaultChannel,
                start,
                end
            )

            eventService.createEventData(request, guild).whenComplete { e, _ ->
                event.reply("Event with Id:" + e.idLong + " has been created.")
                    .setEphemeral(true).queue()

                e.manager.setDescription(
                    "Event has been scheduled to join, please use the " +
                            "`/joinevent eventid:${e.idLong} [starttime] [endtime]` command to let the author know your times of availability."
                )
                    .queue()
            }

        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException, is DateTimeParseException -> {
                    event.reply(
                        "An error occurred setting the **Date**. " + e.message
                                + ". Please check if your chosen dates are valid."
                    )
                        .setEphemeral(true)
                        .queue()
                }
                else -> throw e
            }
        }
    }
}