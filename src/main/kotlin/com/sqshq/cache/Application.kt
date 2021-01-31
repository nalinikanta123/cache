package com.sqshq.cache

import com.sqshq.cache.model.InMemoryApiResponseCache
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import java.net.http.HttpClient

@SpringBootApplication
@EnableScheduling
class Application {

    @Bean
    fun httpClient() = HttpClient.newHttpClient()

    @Bean
    fun inMemoryCache() = InMemoryApiResponseCache()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
