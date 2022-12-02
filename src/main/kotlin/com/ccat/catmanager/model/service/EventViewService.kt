package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.entity.EventParticipantEntity
import org.springframework.data.domain.Range
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.function.BiPredicate
import kotlin.random.Random

@Service
class EventViewService(
    private val eventService: EventService
) {
    fun dateTimeEvaluation(eventId: Long) {
        val response: List<EventParticipantEntity> = eventService.getParticipantDataForEventId(eventId)

        val bestDate: LocalDate = response
            .groupBy({ it.startingTime.toLocalDate() }, { it.userId })
            .maxByOrNull { it.value.size }!!
            .key

//        //for bestDate  :  earliest - latest

        val allTimes: MutableSet<ZonedDateTime> = mutableSetOf()
        val responseByBestDate = response.filter { it.startingTime.toLocalDate().equals(bestDate) }
        responseByBestDate
            .forEach {
                allTimes.add(it.startingTime)
                allTimes.add(it.endingTime)
            }
        val sortedTimes: List<ZonedDateTime> = allTimes.sorted()
        val earliestTime = sortedTimes.first()
        val latestTime = sortedTimes.last()

        val timeRange: ClosedRange<ZonedDateTime> = earliestTime.rangeTo(latestTime)

        var dateTime: ZonedDateTime = earliestTime
        val consideredDateTimeSet: MutableSet<ZonedDateTime> = mutableSetOf()
        while(dateTime in timeRange) {
            consideredDateTimeSet.add(dateTime)
            dateTime = dateTime.plusHours(1)
        }

        val groupedByTime: MutableMap<ZonedDateTime, Set<Long>> = mutableMapOf()
        consideredDateTimeSet.forEach{ currentPointTime ->
            groupedByTime.putAll(responseByBestDate
                .filter { currentPointTime in it.startingTime.rangeTo(it.endingTime) }
                .groupBy({ currentPointTime }, { it.userId })
                .mapValues { it.value.toSet() }
            )
        }

        val maxUsersAvailable: Int = groupedByTime.values.maxByOrNull { it.size }!!.size


        //break into Groups of consecutive:
        groupedByTime.filterValues { it.size == maxUsersAvailable }.keys
            .fold(mutableListOf<ZonedDateTime>() to mutableListOf<List<ZonedDateTime>>() ) { (currentList, accumulator), currItem ->
                if(currentList.isEmpty()) {
                    mutableListOf(currItem) to accumulator
                } else {
                    if(currItem.minusHours(1).equals(currentList.last())) { //group
                        currentList.apply { add(currItem) } to accumulator
                    } else {
                        mutableListOf(currItem) to accumulator.apply { add(currentList) } //next
                    }
                }
            }.let { it.second.apply { it.first } } //convert add list
    }
}
