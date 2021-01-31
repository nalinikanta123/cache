package com.sqshq.cache.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.sqshq.cache.client.ApiClient
import com.sqshq.cache.model.ApiResponseCache
import com.sqshq.cache.model.InMemoryApiResponseCache
import com.sqshq.cache.model.RepositoryData
import com.sqshq.cache.model.RepositoryView
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class CacheService(@Value("\${cache.endpoints}") private val cachedEndpoints: Set<String>,
                   @Value("\${cache.view.source.endpoint}") private val viewEndoint: String,
                   private val cache: ApiResponseCache,
                   private val mapper: ObjectMapper,
                   private val client: ApiClient) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val view = RepositoryView()

    /**
     * Periodically refreshes cache data and triggers views calculation.
     * [PostConstruct] annotation is required to delay application startup
     * until configured endpoints are fully cached
     */
    @PostConstruct
    @Scheduled(
            fixedDelayString = "\${cache.refresh.sec}000",
            initialDelayString = "\${cache.refresh.sec}000")
    fun refresh() {
        logger.debug("Cache refresh triggered")
        cachedEndpoints.forEach { endpoint ->
            cache.put(endpoint, client.requestAll(endpoint))
            if (endpoint == viewEndoint) {
                calculateViews(cache.get(endpoint))
            }
        }
    }

    /**
     * Returns cached result for the requested path, or proxies the request
     * if the endpoint is not configured to be cached
     */
    fun get(path: String): String {

        val normalizedPath = path.removeSuffix("/")

        if (cachedEndpoints.contains(normalizedPath)) {
            return cache.get(normalizedPath)
        }

        return client.request(normalizedPath)
    }

    fun getTopRepositoriesByNumberOfForks(limit: Int) = view.byForks.take(limit)

    fun getTopRepositoriesByNumberStars(limit: Int) = view.byStars.take(limit)

    fun getTopRepositoriesByOpenIssues(limit: Int) = view.byOpenIssues.take(limit)

    fun getTopRepositoriesByLastUpdatedTime(limit: Int) = view.byLastUpdatedTime.take(limit)

    private fun calculateViews(json: String) {

        val repositories: List<ObjectNode> = mapper.readValue(json)

        view.byForks = repositories
                .map { repository -> RepositoryData(repository["full_name"].asText(), repository["forks_count"].asInt()) }
                .sortedByDescending { it.data }

        view.byStars = repositories
                .map { repository -> RepositoryData(repository["full_name"].asText(), repository["stargazers_count"].asInt()) }
                .sortedByDescending { it.data }

        view.byOpenIssues = repositories
                .map { repository -> RepositoryData(repository["full_name"].asText(), repository["open_issues_count"].asInt()) }
                .sortedByDescending { it.data }

        view.byLastUpdatedTime = repositories
                .map { repository -> RepositoryData(repository["full_name"].asText(), repository["updated_at"].asText()) }
                .sortedByDescending { it.data }
    }
}
