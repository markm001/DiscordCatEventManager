package com.ccat.catmanager

import com.ccat.catmanager.listeners.CommandListenerManager
import mu.KLogger
import mu.KotlinLogging
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class JdaConfiguration(
    private val commandListenerManager: CommandListenerManager,
    private val logger: KLogger = KotlinLogging.logger { }
) {
    @Autowired
    lateinit var env: Environment

    companion object {
        lateinit var shardManager: ShardManager
    }

    @PostConstruct
    fun buildJda() {
        val builder = DefaultShardManagerBuilder.createDefault(env.getProperty("TOKEN"))

        builder.setStatus(OnlineStatus.IDLE)
        builder.setActivity(Activity.watching("out for your events"))

        //Intents:
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT)
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES)
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS)

        //Caching:
        builder.setMemberCachePolicy(MemberCachePolicy.ALL)
        builder.setChunkingFilter(ChunkingFilter.ALL)
        builder.enableCache(CacheFlag.ONLINE_STATUS)
        builder.enableCache(CacheFlag.ROLE_TAGS)

        //Listeners:
        builder.addEventListeners(commandListenerManager)

        shardManager = builder.build()

        logger.info("✅ Initialization finished!")
        shardManager.setStatus(OnlineStatus.ONLINE)
    }
}