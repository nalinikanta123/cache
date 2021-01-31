package com.sqshq.cache.model

import java.util.concurrent.ConcurrentHashMap

/**
 * Abstraction for the key-value storage, which
 * stores the cached API responses
 */
interface ApiResponseCache {

    fun put(endpoint: String, response: String)

    fun get(endpoint: String): String
}

/**
 * Default in-memory cache implementation
 */
class InMemoryApiResponseCache : ApiResponseCache {

    private val data = ConcurrentHashMap<String, String>()

    override fun put(endpoint: String, response: String) {
        data[endpoint] = response
    }

    override fun get(endpoint: String): String {
        return data.getValue(endpoint)
    }
}