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
        val args: List<OptionMapping> = event.options

        guild?.defaultChannel?.let {
            guild.createScheduledEvent(
                args[1].asString,
                it.asMention,
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1))
        }
    }
}