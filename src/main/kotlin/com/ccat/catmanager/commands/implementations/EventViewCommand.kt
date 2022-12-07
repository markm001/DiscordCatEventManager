package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.exceptions.EventDataNotFoundException
import com.ccat.catmanager.exceptions.EventIdNotFoundException
import com.ccat.catmanager.model.EventViewResponse
import com.ccat.catmanager.model.service.DateTimeDisplayService
import com.ccat.catmanager.model.service.EventViewService
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.DateTimeException
import java.time.ZoneId

class EventViewCommand(
    override val data: CommandData,
    private val eventViewService: EventViewService,
    private val displayService: DateTimeDisplayService

) : SimpleCommand(data) {
    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()
        val idOption: OptionMapping? = event.getOption("eventid")

        try {
            val eventId: Long = (event.getOption("eventid")?.asLong
                ?: throw EventIdNotFoundException("Id, ${idOption?.asString} was not found"))

            val eventFromId: ScheduledEvent = (event.guild?.getScheduledEventById(eventId)
                ?: throw EventIdNotFoundException("Id:$eventId was not found"))


            val zoneIdOption: OptionMapping? = event.getOption("zoneid")
            val response: EventViewResponse = eventViewService.dateTimeEvaluation(eventId)
                ?: throw EventDataNotFoundException("${eventFromId.name} with Id:[$eventId]")

            var zoneId = ZoneId.systemDefault()
            val evaluation: EventViewResponse = if (zoneIdOption != null) {
                zoneId = ZoneId.of(zoneIdOption.asString)
                displayService.convertToZoneId(response, zoneId)
            } else { response }


            val allUserIds: MutableSet<Long> = mutableSetOf<Long>()
                .apply { addAll(evaluation.participantIds) }
                .apply { addAll(evaluation.excludedIds) }

            event.guild?.retrieveMembersByIds(allUserIds)?.onSuccess { memberList ->

                val evaluationEmbed = EmbedBuilder()
                evaluationEmbed.setTitle(Emoji.fromUnicode("U+1F389").asReactionCode + eventFromId.name)
                evaluationEmbed.setFooter("⚠ All times displayed in $zoneId time")

                // SUGGESTED TIME
                evaluationEmbed.addField(
                    "Suggested Time:",
                    "From: ${displayService.convertToDisplay(evaluation.suggestedStartTime)} " +
                            "\n Until: ${displayService.convertToDisplay(evaluation.suggestedEndTime)}",
                    false
                )

                // PARTICIPANTS
                val participantBuilder = retrieveMembers(evaluation.participantIds, memberList)
                evaluationEmbed.addField(
                    "Assigned Participants:",
                    participantBuilder.toString(),
                    false
                )

                // EARLIEST, LATEST TIME
                evaluationEmbed.addField(
                    "Earliest & latest Time:",
                    "${displayService.convertToDisplay(evaluation.earliestRequestedTime)} " +
                            "| ${displayService.convertToDisplay(evaluation.latestRequestedTime)}",
                    false
                )

                // NON-PARTICIPANTS
                val excludedBuilder = retrieveMembers(evaluation.excludedIds, memberList)
                evaluationEmbed.addField(
                    "Unassigned Members:",
                    excludedBuilder.toString(),
                    false
                )

                event.hook.sendMessageEmbeds(evaluationEmbed.build()).queue()
            }
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
                is EventDataNotFoundException -> {
                    event.hook.sendMessage(
                        "An error occurred retrieving the **Event**. " + ex.message
                                + ". Users may have yet to participate in the event."
                    )
                        .queue()
                }
                is EventIdNotFoundException -> {
                    event.hook.sendMessage(
                        "An error occurred retrieving the requested **Event**" + ex.message
                                + ". Please check that an Event with the Event-Id really exists."
                    )
                        .queue()
                }
                else -> throw ex
            }
        }
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
}