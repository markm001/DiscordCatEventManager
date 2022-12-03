package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.EventIdNotFoundException
import com.ccat.catmanager.model.EventViewResponse
import com.ccat.catmanager.model.service.EventViewService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.DateTimeException
import java.time.ZoneId

class EventViewCommand(
    override val data: CommandData,
    private val eventViewService: EventViewService

) : SimpleCommand(data) {
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()
        val idOption: OptionMapping? = event.getOption("eventid")

        try {
            val eventId: Long = (event.getOption("eventid")?.asLong
                ?: throw EventIdNotFoundException("Event with requested Id, ${idOption?.asString} was not found."))
            val zoneId: ZoneId = (ZoneId.of(event.getOption("zoneid")?.asString)
                ?: ZoneId.systemDefault())

            val evaluation: EventViewResponse = eventViewService.dateTimeEvaluation(eventId, zoneId)

            /**
             * BUILD THE EMBED FROM EVALUATION DATA
             */
            val evaluationEmbed = EmbedBuilder()
            evaluationEmbed.setFooter("All Times in: $zoneId Time")

            event.hook.sendMessageEmbeds(evaluationEmbed.build()).queue()

        } catch (ex: Exception) {
            when (ex) {
                is DateTimeException -> {
                    event.hook.sendMessage(
                        "An error occurred setting the **Timezone**. " + ex.message
                                + ". Please check if your input is a valid Zone-Id."
                    )
                        .queue()
                }
                is NumberFormatException, is IllegalStateException -> {
                    event.hook.sendMessage(
                        "An error occurred parsing the **Event-Id**. " + ex.message
                                + ". Please check that you entered a valid Long value for the Id field."
                    )
                        .queue()
                }
                else -> throw ex
            }
        }
    }
}