package com.ccat.catmanager.util


import com.google.errorprone.annotations.CheckReturnValue
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import java.awt.Color
import java.time.Instant

class ResponseHandler {
    companion object {
        @CheckReturnValue
        fun success(event: IReplyCallback, title: String, message: String): ReplyCallbackAction {
            return reply(event, title, message, EmbedColor.SUCCESS.color, true)
        }

        @CheckReturnValue
        fun success(event: InteractionHook, title: String, message: String): WebhookMessageCreateAction<Message> {
            return reply(event, title, message, EmbedColor.SUCCESS.color, true)
        }

        @CheckReturnValue
        fun error(hook: InteractionHook, message: String): WebhookMessageCreateAction<Message> {
            return reply(
                hook,
                "An **Error** has occurred.",
                message,
                EmbedColor.ERROR.color,
                true
            )
        }

        @CheckReturnValue
        fun error(event: IReplyCallback, message: String): ReplyCallbackAction {
            return reply(
                event,
                "An **Error** has occurred.",
                message,
                EmbedColor.ERROR.color,
                true
            )
        }

        @CheckReturnValue
        fun externalAccessError(event: IReplyCallback, vararg args: Any): ReplyCallbackAction {
            return error(event,
                "Command may only be used **inside a Guild.** $args" )
        }

        private fun reply(hook: InteractionHook, title: String?,
                          message: String, color: Color, ephemeral: Boolean): WebhookMessageCreateAction<Message> {
            return hook.sendMessageEmbeds(buildEmbed(title, message, color)).setEphemeral(ephemeral)
        }

        private fun reply(event: IReplyCallback, title: String?, message: String, color: Color, ephemeral: Boolean) : ReplyCallbackAction {
            return event.replyEmbeds(buildEmbed(title, message, color)).setEphemeral(ephemeral)
        }

        private fun buildEmbed(title: String?, message: String, color: Color) = EmbedBuilder()
            .setTitle(title)
            .setDescription(message)
            .setColor(color)
            .setTimestamp(Instant.now())
            .build()
    }

    enum class EmbedColor(val color: Color) {
        SUCCESS(Color.decode("#12ef0f")),
        ERROR(Color.decode("#ef380f"))
    }
}