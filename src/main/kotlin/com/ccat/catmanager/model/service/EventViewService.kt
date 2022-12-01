package com.ccat.catmanager.model.service

import com.ccat.catmanager.model.entity.EventParticipantEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max

@Service
class EventViewService(
    private val eventService: EventService
) {

    fun dateTimeEvaluation(eventId: Long) {
        val response: List<EventParticipantEntity> = eventService.getParticipantDataForEventId(eventId)

        // most Ids available, map to userId occurrences, find most userIds for Date -> Best Date
        val datesForUserId: HashMap<LocalDate,MutableSet<Long>> = hashMapOf()
        response.forEach {
            val date: LocalDate = it.startingTime.toLocalDate()
            val uid: Long = it.userId

            datesForUserId.getOrPut(date) { mutableSetOf(uid) }
                .add(uid)
        }

        val bestDate: LocalDate = datesForUserId
            .map { Pair(it.key, it.value.size) }
            .maxByOrNull { it.second }!!
            .first //get key


        // most overlaps -> Best Time
        val entriesForDate: List<EventParticipantEntity> = response
            .filter { it.startingTime.toLocalDate().equals(bestDate) }

        val startTimesForUid: HashMap<LocalTime,MutableSet<Long>> = hashMapOf()
        val endTimesForUid: HashMap<LocalTime,MutableSet<Long>> = hashMapOf()
        entriesForDate.forEach {
            val uid: Long = it.userId
            val start: LocalTime = it.startingTime.toLocalTime()
            val end: LocalTime = it.endingTime.toLocalTime()

            startTimesForUid.getOrPut(start) { mutableSetOf(uid) }
                .add(uid)

            endTimesForUid.getOrPut(end) { mutableSetOf(uid) }
                .add(uid)
        }

        val startingTime: LocalTime = startTimesForUid
            .map { Pair(it.key, it.value.size) }
            .maxByOrNull { it.second }!!
            .first

        val endingTime: LocalTime = endTimesForUid
            .map { Pair(it.key, it.value.size) }
            .maxByOrNull { it.second }!!
            .first

    }
}