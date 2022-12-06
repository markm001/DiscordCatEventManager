package com.ccat.catmanager.listeners

enum class CommandEnum(
    val commandName: String
) {
    PING("ping"),
    EVENTCREATE("eventcreate"),
    JOINEVENT("joinevent"),
    TIMESET("timeset"),
    EVENTVIEW("eventview"),
    MANAGE("manage"),
    REMOVE("remove")
}