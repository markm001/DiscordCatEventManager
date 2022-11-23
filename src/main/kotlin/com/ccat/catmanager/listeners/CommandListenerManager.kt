package com.ccat.catmanager.listeners

import com.ccat.catmanager.commands.CommandMapper
import com.ccat.catmanager.commands.SimpleCommand
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Component
class CommandListenerManager (
    val commandMapper: CommandMapper,
    val commandMap: Map<String, SimpleCommand> = commandMapper.commandMap
): ListenerAdapter() {

    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.updateCommands().addCommands(commandMapper.getListCommandData()).queue()
    }


    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        try {
            val command: SimpleCommand = commandMap[event.name] ?: throw NoSuchElementException()
            command.executeCommand(event)
        } catch (e: NoSuchElementException) {
            //TODO: Log this - Shouldn't happen, error in guild.CommandData
            e.printStackTrace()
        }
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val currentOptionName: String = event.focusedOption.name
        val currentInputValue: String = event.focusedOption.value

        if(currentOptionName.contains("time")) {
            val replyOptions: MutableList<Choice> = mutableListOf()

            for(i in 1..25) {
                val day: OffsetDateTime = OffsetDateTime.now().plusDays(i.toLong())
                val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a X")
                val displayDate: String = day.format(format)
                replyOptions.add(Choice(displayDate, day.toString()))
            }

            event.replyChoices(
                if(currentInputValue.isEmpty()) replyOptions
                else replyOptions.filter { it.name.contains(currentInputValue,true) }
            ).queue()
        }

    }
}