package com.sqshq.cache.client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import javax.net.ssl.SSLSession

class TestHttpResponse<T>(private val headers: Map<String, List<String>>, private val response: T, private val statusCode: Int = 200) : HttpResponse<T> {

    override fun headers(): HttpHeaders {
        return HttpHeaders.of(headers) { _, _ -> true }
    }

    override fun body(): T {
        return response
    }

    override fun uri(): URI {
        TODO("Not required")
    }

    override fun sslSession(): Optional<SSLSession> {
        TODO("Not required")
    }

    override fun version(): HttpClient.Version {
        TODO("Not required")
    }

    override fun statusCode(): Int {
        return statusCode
    }

    override fun request(): HttpRequest {
        TODO("Not required")
    }

    override fun previousResponse(): Optional<HttpResponse<T>> {
        TODO("Not required")
    }
}