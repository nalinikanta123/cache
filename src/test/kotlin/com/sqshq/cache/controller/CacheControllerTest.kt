package com.sqshq.cache.controller

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.sqshq.cache.model.RepositoryData
import com.sqshq.cache.service.CacheService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(properties = ["cache.endpoints="])
@AutoConfigureMockMvc
internal class CacheControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: CacheService

    @Test
    fun `should return cached data as is`() {
        //given
        whenever(service.get("/path")).thenReturn("{\"key\": 1}")

        //when
        //then
        mvc.perform(get("/path")
                .contentType("application/json"))
                .andExpect(status().isOk)
                .andExpect(content().string("{\"key\": 1}"))
    }

    @Test
    fun `should return top repositories view by forks`() {
        //given
        whenever(service.getTopRepositoriesByNumberOfForks(any())).thenReturn(listOf(RepositoryData("Netflix/Hystrix", 65), RepositoryData("Netflix/Eureka", 42)))

        //when
        //then
        mvc.perform(get("/view/top/2/forks")
                .contentType("application/json"))
                .andExpect(status().isOk)
                .andExpect(content().string("[[\"Netflix/Hystrix\", 65], [\"Netflix/Eureka\", 42]]"))
    }

    @Test
    fun `should return top repositories view by stars`() {
        //given
        whenever(service.getTopRepositoriesByNumberStars(any())).thenReturn(listOf(RepositoryData("Netflix/Hystrix", 32161), RepositoryData("Netflix/Eureka", 12452)))

        //when
        //then
        mvc.perform(get("/view/top/2/stars")
                .contentType("application/json"))
                .andExpect(status().isOk)
                .andExpect(content().string("[[\"Netflix/Hystrix\", 32161], [\"Netflix/Eureka\", 12452]]"))
    }

    @Test
    fun `should return top repositories view by open issues`() {
        //given
        whenever(service.getTopRepositoriesByOpenIssues(any())).thenReturn(listOf(RepositoryData("Netflix/Hystrix", 124), RepositoryData("Netflix/Eureka", 125)))

        //when
        //then
        mvc.perform(get("/view/top/2/open_issues")
                .contentType("application/json"))
                .andExpect(status().isOk)
                .andExpect(content().string("[[\"Netflix/Hystrix\", 124], [\"Netflix/Eureka\", 125]]"))
    }

    @Test
    fun `should return top repositories view by update time`() {
        //given
        whenever(service.getTopRepositoriesByLastUpdatedTime(any())).thenReturn(listOf(RepositoryData("Netflix/Hystrix", "2021-01-24T06:31:02Z"), RepositoryData("Netflix/Eureka", "2020-02-24T06:21:14Z")))

        //when
        //then
        mvc.perform(get("/view/top/2/last_updated")
                .contentType("application/json"))
                .andExpect(status().isOk)
                .andExpect(content().string("[[\"Netflix/Hystrix\", \"2021-01-24T06:31:02Z\"], [\"Netflix/Eureka\", \"2020-02-24T06:21:14Z\"]]"))
    }
}