package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.OffsetDateTime

class CreateEventCommand(
    override val data: CommandData
) : SimpleCommand(data) {
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        val guild: Guild? = event.guild
        //TODO: Make valid exceptions
        val topic: OptionMapping = event.getOption("topic") ?: throw Throwable("Invalid Argument")
        val start: OffsetDateTime = OffsetDateTime
            .parse(event.getOption("starttime")!!.asString) ?: throw Throwable("Invalid start time")
        val end: OffsetDateTime = OffsetDateTime
            .parse(event.getOption("endtime")!!.asString) ?: throw Throwable("Invalid end time")

        guild?.defaultChannel?.let {
            guild.createScheduledEvent(
                topic.asString,
                it.asMention,
                start,
                end
            ).queue()
        }
    }
}