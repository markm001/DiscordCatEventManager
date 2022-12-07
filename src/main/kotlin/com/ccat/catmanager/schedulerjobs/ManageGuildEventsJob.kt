package com.ccat.catmanager.schedulerjobs

import com.ccat.catmanager.JdaConfiguration
import com.ccat.catmanager.model.EventViewResponse
import com.ccat.catmanager.model.ManagedEventRequest
import com.ccat.catmanager.model.entity.ManagedEventEntity
import com.ccat.catmanager.model.service.EventViewService
import com.ccat.catmanager.model.service.ManagedEventService
import net.dv8tion.jda.api.entities.Guild
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class ManageGuildEventsJob(
    private val eventService: ManagedEventService,
    private val viewService: EventViewService,
    private val expiredEventList: MutableList<ManagedEventEntity>
) {
    @Scheduled(cron = "0 */10 * * * ?")
    fun updateEventBoard() {
        println("++ Retrieving Data ++")
        val events: List<ManagedEventEntity> = eventService.findAllManagedEvents()
        events.forEach { println("Managing: ${it.eventId} in Guild: ${it.guildId}") }

        events.forEach { eventEntity ->
            //TODO: LOG THIS!
            val guild: Guild = JdaConfiguration.shardManager.getGuildById(eventEntity.guildId)
                ?: throw Exception("This shouldn't happen! Incorrectly saved.")

            val eventViewData: EventViewResponse = viewService.dateTimeEvaluation(eventEntity.eventId)
                ?: return@forEach

            guild.retrieveScheduledEventById(eventEntity.eventId).queue({
                it.manager
                    .setStartTime(eventViewData.suggestedStartTime)
                    .setEndTime(eventViewData.suggestedEndTime)
                    .setDescription("The event is currently being managed by the bot.").queue()
            },
                {
                    //TODO: LOG THIS
                    expiredEventList.add(eventEntity)
                    return@queue
                }
            )
        }

        //CLEAN-UP QUEUE FOR EXPIRED:
        expiredEventList.map {
            ManagedEventRequest(
                it.eventId,
                it.guildId
            )
        }
            .forEach { cleanManagedEventQueue(it) }
    }

    private fun cleanManagedEventQueue(request: ManagedEventRequest) {
        eventService.removeManagedEvent(request)
    }
}