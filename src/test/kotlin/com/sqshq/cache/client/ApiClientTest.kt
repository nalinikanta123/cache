package com.sqshq.cache.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.io.IOException
import java.lang.IllegalStateException
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@ExtendWith(MockitoExtension::class)
internal class ApiClientTest {

    @Mock
    private lateinit var httpClient: HttpClient

    private lateinit var apiClient: ApiClient

    private lateinit var mapper: ObjectMapper

    @BeforeEach
    fun setup() {
        mapper = ObjectMapper()
        apiClient = ApiClient("https://api.github.com", "application/vnd.github.v3+json", "SECRET", 1, httpClient, mapper)
    }

    @Test
    fun `should request by given path`() {
        //given
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>())).thenReturn(TestHttpResponse(mapOf(), ""))

        //when
        apiClient.request("/orgs/Netflix")

        //then
        argumentCaptor<HttpRequest>().apply {
            verify(httpClient).send(capture(), any<HttpResponse.BodyHandler<String>>())
            assertEquals("https://api.github.com/orgs/Netflix", firstValue.uri().toString())
            assertEquals("application/vnd.github.v3+json", firstValue.headers().map()["Accept"]?.first())
            assertEquals("token SECRET", firstValue.headers().map()["Authorization"]?.first())
        }
    }

    @Test
    fun `should paginate through all available content when there is an array response`() {
        //given
        val header = mapOf("link" to listOf("<https://api.github.com/orgs/Netflix/members?page=2&per_page=1>; rel=\"next\""))
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
                .thenReturn(TestHttpResponse(header, "[{\"key1\": \"value1\"}]"))
                .thenReturn(TestHttpResponse(emptyMap(), "[{\"key2\": \"value2\"}]"))

        //when
        val result = apiClient.requestAll("/orgs/Netflix/members")

        //then
        argumentCaptor<HttpRequest>().apply {
            verify(httpClient, times(2)).send(capture(), any<HttpResponse.BodyHandler<String>>())
        }
        val value: List<ObjectNode> = mapper.readValue(result)
        assertEquals(2, value.size)
        assertEquals("value1", value.first()["key1"].asText())
        assertEquals("value2", value.last()["key2"].asText())
    }

    @Test
    fun `should paginate through all available content when there is an object response`() {
        //given
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()))
                .thenReturn(TestHttpResponse(emptyMap(), "{\"key1\": \"value1\"}"))

        //when
        val result = apiClient.requestAll("/orgs/Netflix/members")

        //then
        argumentCaptor<HttpRequest>().apply {
            verify(httpClient, times(1)).send(capture(), any<HttpResponse.BodyHandler<String>>())
        }
        val value: ObjectNode = mapper.readValue(result)
        assertEquals("value1", value["key1"].asText())
    }

    @Test
    fun `should return true for healthcheck when API responds with OK status`() {
        //given
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>())).thenReturn(TestHttpResponse(mapOf(), "", HttpStatus.OK.value()))

        //when
        val healthcheck = apiClient.healthcheck()

        //then
        assertTrue(healthcheck)
    }

    @Test
    fun `should fail healthcheck when API responds with non-OK status`() {
        //given
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>())).thenReturn(TestHttpResponse(mapOf(), "", HttpStatus.SERVICE_UNAVAILABLE.value()))

        //when
        val healthcheck = apiClient.healthcheck()

        //then
        assertFalse(healthcheck)
    }

    @Test
    fun `should fail healthcheck when request fails`() {
        //given
        whenever(httpClient.send(any(), any<HttpResponse.BodyHandler<String>>())).thenThrow(IOException())

        //when
        val healthcheck = apiClient.healthcheck()

        //then
        assertFalse(healthcheck)
    }
}