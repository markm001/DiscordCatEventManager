package com.ccat.catmanager.model.service

import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import org.springframework.stereotype.Service
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class AutocompleteService {

    private val datePattern: Pattern = "[12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])".toPattern()
    private val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

    /**
     * Filter choices by inputValue
     * extract inputValue date by " " [2022-02-03 0...]
     * date.matches(Pattern) -> 2022-02-03 00:00 (from 00:00AM) : now()
     * date.matches(Pattern) -> suggest Times : suggest Dates \ 2022-02-03 00:00  ||  2022-02-... 12:58
     * format to 2022-11-28 01:00 am -> return max.25 unique choices
     */
    fun completeDateTime(inputValue: String): Set<Choice> {
        val replyOptions: MutableSet<Choice> = mutableSetOf()
        val currentTime: LocalDateTime = LocalDateTime.now()

        val dateMatcher: Matcher = datePattern.matcher(
            if(inputValue.contains(" ")) {
                inputValue.split(" ")[0]
            } else {inputValue})

        val referenceTime: LocalDateTime =
            if (dateMatcher.matches()) {
                LocalDateTime.of(
                    try { LocalDate.parse(dateMatcher.group(0)) } catch (e: DateTimeParseException) { LocalDate.now()},
                    LocalTime.MIDNIGHT
                )
            } else {
                currentTime
            }

        val incrementUnit: ChronoUnit = if (dateMatcher.matches()) {
            ChronoUnit.HOURS
        } else {
            ChronoUnit.DAYS
        }

        for(i in 0..24) {
            val suggestedDateTime: LocalDateTime = referenceTime.plus(i.toLong(), incrementUnit)
            val displayDateTime: String = suggestedDateTime.format(format)
            replyOptions.add(Choice(displayDateTime, suggestedDateTime.toString()))
        }

        return replyOptions.filter { it.name.contains(inputValue, true) }.toSet()
    }

    /**
     * All Guild-ScheduledEvents -> filter(name) -> take max 25 -> return unique mapped to Choice(name,id)
     */
    fun completeScheduledGuildEvents(allGuildEvents: List<ScheduledEvent>, inputValue: String): Set<Choice> {
        return allGuildEvents
            .filter { it.name.contains(inputValue) }
            .take(25)
            .map { Choice(it.name, it.idLong) }
            .toSet()
    }
}