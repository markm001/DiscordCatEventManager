package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.EventDataNotFoundException
import com.ccat.catmanager.model.EventViewResponse
import com.ccat.catmanager.model.service.DateTimeDisplayService
import com.ccat.catmanager.model.service.EventViewService
import com.ccat.catmanager.util.ResponseHandler
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.ErrorResponse
import java.time.DateTimeException
import java.time.ZoneId

class EventViewCommand(
    override val data: CommandData,
    private val eventViewService: EventViewService,
    private val displayService: DateTimeDisplayService

) : SimpleCommand(data) {
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        try {
            val eventId: Long = event.getOption("eventid")!!.asLong

            event.guild?.retrieveScheduledEventById(eventId)?.queue(
                { eventFromId ->
                    val response: EventViewResponse = eventViewService.dateTimeEvaluation(eventId)
                        ?: throw EventDataNotFoundException("${eventFromId.name} with Id:[$eventId]")

                    val zoneId = ZoneId.of(event.getOption("zoneid")?.asString) ?: ZoneId.systemDefault()
                    val evaluation: EventViewResponse = displayService.convertToZoneId(response, zoneId)

                    val allUserIds: MutableSet<Long> = mutableSetOf<Long>()
                        .apply { addAll(evaluation.participantIds) }
                        .apply { addAll(evaluation.excludedIds) }

                    event.guild?.retrieveMembersByIds(allUserIds)?.onSuccess { memberList ->
                        val evaluationEmbed = buildEvaluationEmbed(evaluation, memberList)
                        evaluationEmbed.setTitle(Emoji.fromUnicode("U+1F389").asReactionCode + eventFromId.name)
                        evaluationEmbed.setFooter("⚠ All times displayed in $zoneId time")
                    }
                },
                handleErrors(event)
            )
        } catch (e:Exception) {
            when(e) {
                is NumberFormatException -> {
                    ResponseHandler.error(
                        event.hook,
                        e.message ?: "Please check if the **Event-Id** is valid."
                    ).queue()
                }
                is EventDataNotFoundException -> {
                    ResponseHandler.error(
                        event.hook,
                        "${e.message} Users may have yet to participate in this event."
                    ).queue()
                }
            }
        }
    }

    private fun buildEvaluationEmbed(evaluation: EventViewResponse, members: MutableList<Member>): EmbedBuilder {
        val evaluationEmbed = EmbedBuilder()

        // SUGGESTED TIME
        evaluationEmbed.addField(
            "Suggested Time:",
            "From: ${displayService.display(evaluation.suggestedStartTime)} " +
                    "\n Until: ${displayService.display(evaluation.suggestedEndTime)}",
            false
        )

        // PARTICIPANTS
        val participantBuilder = retrieveMembers(evaluation.participantIds, members)
        evaluationEmbed.addField(
            "Assigned Participants:",
            participantBuilder.toString(),
            false
        )

        // EARLIEST, LATEST TIME
        evaluationEmbed.addField(
            "Earliest & latest Time:",
            "${displayService.display(evaluation.earliestRequestedTime)} " +
                    "| ${displayService.display(evaluation.latestRequestedTime)}",
            false
        )

        // NON-PARTICIPANTS
        val excludedBuilder = retrieveMembers(evaluation.excludedIds, members)
        evaluationEmbed.addField(
            "Unassigned Members:",
            excludedBuilder.toString(),
            false
        )

        return evaluationEmbed
    }

    private fun retrieveMembers(memberIdList: Set<Long>, memberList: MutableList<Member>): StringBuilder {
        val participantBuilder = StringBuilder()
        memberIdList.forEach { id ->
            //TODO: LOG THIS!
            val memberFromId: Member = memberList.find { it.idLong == id } ?: throw Exception()
            participantBuilder.append("${memberFromId.asMention} |")
        }
        return participantBuilder
    }

    private fun handleErrors(event: SlashCommandInteractionEvent) =
        ErrorHandler()
            .handle(ErrorResponse.SCHEDULED_EVENT) {
                ResponseHandler.error(
                    event.hook,
                    it.message ?: "Please check if the chosen **Dates** are valid."
                ).queue()
            }
            .handle(DateTimeException::class.java) {
                ResponseHandler.error(
                    event.hook,
                    it.message ?: "Please check if the chosen **Dates** are valid."
                ).queue()
            }
}