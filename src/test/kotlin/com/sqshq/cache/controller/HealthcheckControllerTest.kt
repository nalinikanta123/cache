package com.sqshq.cache.controller

import com.nhaarman.mockitokotlin2.whenever
import com.sqshq.cache.client.ApiClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(properties = ["cache.endpoints="])
@AutoConfigureMockMvc
internal class HealthcheckControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var apiClient: ApiClient

    @Test
    fun `should return status 200 when service is healthy`() {
        //given
        whenever(apiClient.healthcheck()).thenReturn(true)

        //when
        //then
        mvc.perform(MockMvcRequestBuilders.get("/healthcheck")
                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should return status 503 when service is not healthy`() {
        //given
        whenever(apiClient.healthcheck()).thenReturn(false)

        //when
        //then
        mvc.perform(MockMvcRequestBuilders.get("/healthcheck")
                .contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable)
    }
}