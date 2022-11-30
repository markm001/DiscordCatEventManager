package com.ccat.catmanager.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration {

    @Bean
    fun configureCacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager("zoneIdForUsers")
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES))

        return cacheManager
    }
}