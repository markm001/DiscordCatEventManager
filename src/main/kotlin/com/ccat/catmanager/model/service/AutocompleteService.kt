package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.entity.UserTimezoneEntity
import com.ccat.catmanager.model.repository.UserTimezoneDao
import net.dv8tion.jda.api.entities.ScheduledEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class AutocompleteService(
    private val userTimezoneDao: UserTimezoneDao,
) {

    private val datePattern: Pattern = "[12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])".toPattern()
    private val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

    /**
     * Find (cached/database) userZoneId result || use systemDefault zoneId
     * use referenceTime + 1h : 00 : 0 : 0 (round to next full hour)
     * currentTime(zoneId) -> filter all Results before currentTime
     * date.matches(Pattern) -> 2022-02-03 00:00 (from 00:00AM) : now()
     * date.matches(Pattern) -> suggest Times : suggest Dates \ 2023-02-03 00:00  ||  2023-02-... 12:00
     */
    fun completeDateTime(inputValue: String, userId: Long): Set<Choice> {
        val userZoneId = userTimezoneDao.findById(userId)
            .orElseGet { UserTimezoneEntity(userId, ZoneId.systemDefault()) }.zoneId

        val currentUserTime: LocalDateTime = LocalDateTime.now(userZoneId)
            .plusHours(1)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val dateMatcher: Matcher = datePattern.matcher(inputValue.split(" ")[0])

        val referenceTime: LocalDateTime =
            if (dateMatcher.matches()) {
                LocalDateTime.of(
                    try { LocalDate.parse(dateMatcher.group(0)) } catch (e: DateTimeParseException) { LocalDate.now() },
                    LocalTime.MIDNIGHT
                )
            } else {
                currentUserTime
            }

        val incrementUnit: ChronoUnit = if (dateMatcher.matches()) { ChronoUnit.HOURS } else { ChronoUnit.DAYS }

        val suggestedDates: MutableList<LocalDateTime> = mutableListOf()
        for(i in 0..24) {
            suggestedDates.add(referenceTime.plus(i.toLong(), incrementUnit))
        }

        return suggestedDates
            .filter { it.isAfter(currentUserTime) }
            .map { Choice(it.format(format), it.toString()) }
            .filter { it.name.contains(inputValue, true) }
            .toSet()
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

    /**
     * All zoneIds -> filter(name) -> take max 25 -> return unique Choice(name,zoneId)
     */
    fun completeZoneId(inputValue: String): Set<Choice> {
        return ZoneId.getAvailableZoneIds()
            .filter { it.contains(inputValue) }
            .take(25)
            .map { Choice(it.toString(), it) }
            .toSet()
    }
}