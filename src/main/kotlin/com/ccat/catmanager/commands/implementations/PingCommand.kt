package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.ServerCommand
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.concurrent.TimeUnit

class PingCommand: ServerCommand {
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        val channel: TextChannel = event.channel.asTextChannel()
        val member: Member? = event.member

        channel.sendMessage("Pong for " + member?.asMention)
            .onSuccess { m -> m.delete().queueAfter(20, TimeUnit.SECONDS) }.queue()

        event.reply("Ping has been sent.").setEphemeral(true).queue()
    }
}