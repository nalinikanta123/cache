package com.sqshq.cache.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class RepositoryDataTest {

    private val mapper = ObjectMapper()

    @Test
    fun `should return valid json as toString() output for Int payload`() {
        //given
        val repositoryData = RepositoryData("Netflix/Hystrix", 123)

        //when
        val toString = repositoryData.toString()

        //then
        val tree = mapper.readTree(toString)
        assertTrue(tree.isArray)
        assertEquals(repositoryData.name, tree.get(0).asText())
        assertEquals(repositoryData.data, tree.get(1).asInt())
    }

    @Test
    fun `should return valid json as toString() output for String payload`() {
        //given
        val repositoryData = RepositoryData("Netflix/Hystrix", "2020-01-24T06:21:12Z")

        //when
        val toString = repositoryData.toString()

        //then
        val tree = mapper.readTree(toString)
        assertTrue(tree.isArray)
        assertEquals(repositoryData.name, tree.get(0).asText())
        assertEquals(repositoryData.data, tree.get(1).asText())
    }
}