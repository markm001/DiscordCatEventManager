package com.ccat.catmanager.listeners

import com.ccat.catmanager.commands.CommandMapper
import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.model.service.AutocompleteService
import mu.KLogger
import mu.KotlinLogging
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import org.springframework.stereotype.Component

@Component
class CommandListenerManager(
    val commandMapper: CommandMapper,
    val commandMap: Map<String, SimpleCommand> = commandMapper.commandMap,
    val autocompleteService: AutocompleteService,

    private val logger: KLogger = KotlinLogging.logger { }
) : ListenerAdapter() {

    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.updateCommands().addCommands(commandMapper.getListCommandData()).queue()
    }


    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        try {
            val command: SimpleCommand = commandMap[event.name] ?: throw NoSuchElementException()
            command.executeCommand(event)
        } catch (e: NoSuchElementException) {
            logger.error("guild.CommandData Error - A Slash-Command with the requested signature was not found!")
        }
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val currentOptionName: String = event.focusedOption.name
        val currentInputValue: String = event.focusedOption.value

        val choicesList: Set<Choice> =
            with(currentOptionName) {
                when {
                    contains("time") -> autocompleteService.completeDateTime(currentInputValue, event.user.idLong)
                    equals("eventid") -> autocompleteService
                        .completeScheduledGuildEvents(event.guild!!.scheduledEvents, currentInputValue)
                    equals("zoneid") -> autocompleteService.completeZoneId(currentInputValue)
                    else -> setOf()
                }
            }
        event.replyChoices(choicesList).queue()
    }
}