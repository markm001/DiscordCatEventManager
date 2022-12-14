package com.ccat.catmanager.commands.implementations

import com.ccat.catmanager.commands.SimpleCommand
import com.ccat.catmanager.model.entity.UserTimezoneEntity
import com.ccat.catmanager.model.repository.UserTimezoneDao
import com.ccat.catmanager.util.ResponseHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.DateTimeException
import java.time.ZoneId

class SetTimezoneCommand(
    override val data: CommandData,
    private val userTimezoneDao: UserTimezoneDao
) : SimpleCommand(data) {

    override fun executeCommand(event: SlashCommandInteractionEvent) {
        event.deferReply().setEphemeral(true).queue()

        try {
            val userZoneId: ZoneId = ZoneId.of(event.getOption("zoneid")!!.asString)
            val userId: Long = event.user.idLong

            val response: UserTimezoneEntity = userTimezoneDao.save(
                UserTimezoneEntity(
                    userId,
                    userZoneId
                )
            )

            ResponseHandler.success(
                event.hook,
                "Timezone saved.",
                "Timezone: ${response.zoneId} has been saved for UserId: ${response.userId}"
            ).queue()

        } catch (ex: Exception) {
            when (ex) {
                is DateTimeException -> {
                    ResponseHandler.error(
                        event.hook,
                        ex.message ?: "An error occurred setting the **Timezone**. Please check if your input Zone-Id is valid."
                    ).queue()
                }
                else -> throw ex
            }
        }
    }
}