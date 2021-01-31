package com.sqshq.cache.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.whenever
import com.sqshq.cache.client.ApiClient
import com.sqshq.cache.model.ApiResponseCache
import com.sqshq.cache.model.InMemoryApiResponseCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class CacheServiceTest {

    @Mock
    private lateinit var client: ApiClient

    private lateinit var service: CacheService

    private lateinit var mapper: ObjectMapper

    private lateinit var cache: ApiResponseCache

    @BeforeEach
    fun setup() {
        mapper = ObjectMapper()
        cache = InMemoryApiResponseCache()
        service = CacheService(setOf("/repos"), "/repos", cache, mapper, client)
        whenever(client.requestAll("/repos")).thenReturn(
                """[{
                    "full_name": "Netflix/astyanax",
                    "stargazers_count": 1012,
                    "forks_count": 42,
                    "open_issues_count": 244,
                    "updated_at": "2021-01-21T07:23:13Z"
                },
                {
                    "full_name": "Netflix/Cloud-Prize",
                    "stargazers_count": 2320,
                    "forks_count": 12,
                    "open_issues_count": 27,
                    "updated_at": "2019-01-24T06:21:12Z"
                },
                {
                    "full_name": "Netflix/Hystrix",
                    "stargazers_count": 14232,
                    "forks_count": 714,
                    "open_issues_count": 142,
                    "updated_at": "2020-01-24T06:21:12Z"
                }]
            """
        )
    }

    @Test
    fun `should get data by a non-cached path`() {
        //given
        service.refresh()
        whenever(client.request("/notcached")).thenReturn("{result}")

        //when
        val result = service.get("/notcached")

        //then
        assertEquals("{result}", result)
    }

    @Test
    fun `should get data by a cached path`() {
        //given
        service.refresh()

        //when
        val result = service.get("/repos")

        //then
        val value: List<ObjectNode> = mapper.readValue(result)
        assertEquals(3, value.size)
        assertEquals("Netflix/astyanax", value.first().get("full_name").asText())
        assertEquals("Netflix/Hystrix", value.last().get("full_name").asText())
    }

    @Test
    fun `should return view by forks`() {
        //given
        service.refresh()

        //when
        val result = service.getTopRepositoriesByNumberOfForks(2)

        //then
        assertEquals(2, result.size)
        assertEquals("Netflix/Hystrix", result[0].name)
        assertEquals(714, result[0].data)
        assertEquals("Netflix/astyanax", result[1].name)
        assertEquals(42, result[1].data)
    }

    @Test
    fun `should return view by stars`() {
        //given
        service.refresh()

        //when
        val result = service.getTopRepositoriesByNumberStars(2)

        //then
        assertEquals(2, result.size)
        assertEquals("Netflix/Hystrix", result[0].name)
        assertEquals(14232, result[0].data)
        assertEquals("Netflix/Cloud-Prize", result[1].name)
        assertEquals(2320, result[1].data)
    }

    @Test
    fun `should return view by open issues`() {
        //given
        service.refresh()

        //when
        val result = service.getTopRepositoriesByOpenIssues(2)

        //then
        assertEquals(2, result.size)
        assertEquals("Netflix/astyanax", result[0].name)
        assertEquals(244, result[0].data)
        assertEquals("Netflix/Hystrix", result[1].name)
        assertEquals(142, result[1].data)
    }

    @Test
    fun `should return view by last updated time`() {
        //given
        service.refresh()

        //when
        val result = service.getTopRepositoriesByLastUpdatedTime(2)

        //then
        assertEquals(2, result.size)
        assertEquals("Netflix/astyanax", result[0].name)
        assertEquals("2021-01-21T07:23:13Z", result[0].data)
        assertEquals("Netflix/Hystrix", result[1].name)
        assertEquals("2020-01-24T06:21:12Z", result[1].data)
    }
}