package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.util.concurrent.TimeUnit

class PingCommand(
    override val data: CommandData
) : SimpleCommand(data) {
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        val channel: TextChannel = event.channel.asTextChannel()
        val member: Member? = event.member

        channel.sendMessage("Pong for " + member?.asMention + ", " + event.jda.gatewayPing + "ms")
            .onSuccess { m -> m.delete().queueAfter(20, TimeUnit.SECONDS) }.queue()

        event.reply("Ping has been sent.").setEphemeral(true).queue()
    }
}