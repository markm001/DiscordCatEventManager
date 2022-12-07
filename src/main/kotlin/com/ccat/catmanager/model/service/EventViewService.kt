package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.EventViewResponse
import com.ccat.catmanager.model.entity.EventParticipantEntity
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

@Service
class EventViewService(
    private val eventService: EventService
) {
    /**
     * ResponseEntities: earliest - latest Time-Range -> generate Map<Time, List<UserId>>
     * Take only most Participants -> Group consecutive Entries -> Most consecutive(overlapping) = Best Time
     * Retrieve: Participants & Excluded
     */
    fun dateTimeEvaluation(eventId: Long): EventViewResponse? {
        val response: List<EventParticipantEntity> = eventService.getParticipantDataForEventId(eventId)

        if(response.isEmpty()) { return null }

        val startAndEndTime: Pair<ZonedDateTime, ZonedDateTime> = response
            .fold<EventParticipantEntity, MutableSet<ZonedDateTime>>(mutableSetOf()) { accu, item ->
                accu.apply { addAll(setOf(item.startingTime, item.endingTime)) }
            }
            .sorted() // sort by time
            .let { it.first() to it.last() } // earliest - latest

        val earliestTime = startAndEndTime.first
        val latestTime = startAndEndTime.second

        // [ 2022-12-02T19:00Z[UTC] , 2022-12-02T20:00Z[UTC] , 2022-12-02T21:00Z[UTC] , ... 2022-12-03T04:00Z[UTC] ]
        var dateTime: ZonedDateTime = earliestTime
        val timeRangeIncrements: MutableSet<ZonedDateTime> = mutableSetOf()
        while (dateTime in earliestTime.rangeTo(latestTime)) {
            timeRangeIncrements.add(dateTime)
            dateTime = dateTime.plusHours(1)
        }

        /**
         * 2022-12-02T19:00Z[UTC] - [539078494252957005, 539078494252957004, ...]
         * 2022-12-02T20:00Z[UTC] - [539078494252957001, 539078494252957005, 539078494252957004, ...]
         */
        val groupedByTime: MutableMap<ZonedDateTime, Set<Long>> = mutableMapOf()
        timeRangeIncrements.forEach { currentPointTime ->
            groupedByTime.putAll(response
                .filter { currentPointTime in it.startingTime.rangeTo(it.endingTime) }
                .groupBy({ currentPointTime }, { it.userId })
                .mapValues { it.value.toSet() }
            )
        }

        val maxUsersAvailable: Int = groupedByTime.values.maxByOrNull { it.size }!!.size

        // Group consecutive entries (with most Users): -> startTime - endTime
        val eventTimeWindow: Pair<ZonedDateTime, ZonedDateTime> =
            groupedByTime.filterValues { it.size == maxUsersAvailable }.keys
                .fold(mutableListOf<ZonedDateTime>() to mutableListOf<List<ZonedDateTime>>()) { (currentList, accumulator), currTime ->
                    if (currentList.isEmpty()) {
                        mutableListOf(currTime) to accumulator
                    } else {
                        if (currentList.last().plusHours(1).equals(currTime)) { //group
                            currentList.apply { add(currTime) } to accumulator
                        } else {
                            mutableListOf(currTime) to accumulator.apply { add(currentList) } //next
                        }
                    }
                }.let { it.second.apply { add(it.first) } } //convert add list
                .maxByOrNull { it.size }
                .let { it!!.first() to it.last() }

        val participantIds: Set<Long> = groupedByTime
            .filterKeys { it in eventTimeWindow.first.rangeTo(eventTimeWindow.second) }
            .flatMap { it.value }
            .toSet()

        val excludedIds: Set<Long> = response
            .filter { !participantIds.contains(it.userId) }
            .map { it.userId }
            .toSet()

        return EventViewResponse(
            participantIds,
            excludedIds,
            eventTimeWindow.first,
            eventTimeWindow.second,
            earliestTime,
            latestTime
        )
    }
}
