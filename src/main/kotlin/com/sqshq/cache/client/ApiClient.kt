package com.sqshq.cache.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.lang.Exception
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.math.log

/**
 * Rest client for Github API
 */
@Component
class ApiClient(@Value("\${github.url}") private val url: String,
                @Value("\${github.accept.header}") private val acceptHeader: String,
                @Value("\${github.api.token:#{null}}") private val apiToken: String?,
                @Value("\${github.page.size}") private val pageSize: Int,
                private val client: HttpClient,
                private val mapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun healthcheck(): Boolean {
        logger.trace("API healthcheck: {}", url)
        return try {
            execute(URI.create(url)).statusCode() == HttpStatus.OK.value()
        } catch (e: Exception) {
            logger.error("Failed to perform a healthcheck", e)
            false
        }
    }

    /**
     * Returns the exact API response from the server
     */
    fun request(path: String): String {
        logger.trace("API request: {}", path)
        return execute(URI.create(url + path)).body()
    }

    /**
     * Paginates through all available content and flattens
     * the result into a single string (valid json array)
     * In case when the endpoint responds with a single object,
     * returns it as is, without an attempt to paginate
     */
    fun requestAll(path: String): String {

        logger.trace("API bulk request: {}", path)

        var uri: URI? = UriComponentsBuilder.fromUriString(url + path)
                .queryParam("per_page", pageSize)
                .build()
                .toUri()

        val result = mutableListOf<ObjectNode>()

        while (uri != null) {
            val response = execute(uri)
            if (!isArrayResponse(response)) {
                return response.body()
            }
            uri = getNextPageLink(response)
            result.addAll(mapper.readValue(response.body()))
        }

        return result.toString()
    }

    private fun isArrayResponse(response: HttpResponse<String>): Boolean {
        return mapper.readTree(response.body()).isArray
    }

    private fun execute(uri: URI): HttpResponse<String> {

        val request = HttpRequest.newBuilder()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, acceptHeader)

        if (apiToken != null) {
            request.header(HttpHeaders.AUTHORIZATION, "token $apiToken")
        }

        return client.send(request.build(), HttpResponse.BodyHandlers.ofString())
    }
}