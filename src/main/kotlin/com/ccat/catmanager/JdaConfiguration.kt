package com.ccat.catmanager

import com.ccat.catmanager.listeners.CommandListenerManager
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class JdaConfiguration(
    private val commandListenerManager: CommandListenerManager
) {
    @Autowired
    lateinit var env: Environment

    lateinit var shardManager: ShardManager

    @PostConstruct
    @ConfigurationProperties("discord")
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

        println("\n ✅ Initialization finished!")
        shardManager.setStatus(OnlineStatus.ONLINE)
    }
}