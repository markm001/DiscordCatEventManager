package com.ccat.catmanager.commands

import com.ccat.catmanager.commands.implementations.PingCommand
import com.ccat.catmanager.listeners.CommandEnum
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component

@Component
class CommandMapper (
    val commandMap: Map<String, SimpleCommand> = mapOf(
        CommandEnum.PING.commandName to PingCommand(
            Commands.slash(CommandEnum.PING.commandName, "Send a test ping to yourself."))
    )
) {
    fun getListCommandData() : List<CommandData> {
        return commandMap.map { it.value.data }
    }
}