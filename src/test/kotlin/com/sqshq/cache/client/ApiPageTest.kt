package com.sqshq.cache.client

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ApiPageTest {

    @Test
    fun `should parse valid next page link`() {
        //given
        val header = mapOf("link" to listOf(
                "<https://api.github.com/organizations/913567/members?page=2&per_page=1>; rel=\"next\", " +
                        "<https://api.github.com/organizations/913567/members?page=23&per_page=1>; rel=\"last\""))
        //when
        val link = getNextPageLink(TestHttpResponse(header, ""))
        //then
        assertEquals(link.toString(), "https://api.github.com/organizations/913567/members?page=2&per_page=1")
    }

    @Test
    fun `should parse valid next page link when order is different`() {
        //given
        val header = mapOf("link" to listOf(
                " <https://api.github.com/organizations/913567/members?page=23&per_page=1>; rel=\"last\"   , " +
                        " <https://api.github.com/organizations/913567/members?page=2&per_page=1>;  rel=\"next\" "))
        //when
        val link = getNextPageLink(TestHttpResponse(header, ""))
        //then
        assertEquals(link.toString(), "https://api.github.com/organizations/913567/members?page=2&per_page=1")
    }

    @Test
    fun `should return null when next link is missing`() {
        //given
        val header = mapOf("link" to listOf("<https://api.github.com/organizations/913567/members?page=23&per_page=1>; rel=\"last\""))
        //when
        val link = getNextPageLink(TestHttpResponse(header, ""))
        //then
        assertNull(link)
    }

    @Test
    fun `should return null when next link is malformed`() {
        //given
        val header = mapOf("link" to listOf("<https://api.github.com/organizations/913567/members?page=2&per_page=1; rel=\"next\""))
        //when
        val link = getNextPageLink(TestHttpResponse(header, ""))
        //then
        assertNull(link)
    }

    @Test
    fun `should return null when header is not present`() {
        //given
        val header = mapOf("link" to emptyList<String>())
        //when
        val link = getNextPageLink(TestHttpResponse(header, ""))
        //then
        assertNull(link)
    }
}