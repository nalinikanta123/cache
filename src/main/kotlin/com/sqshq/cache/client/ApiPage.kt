package com.sqshq.cache.client

import java.net.URI
import java.net.http.HttpResponse

private const val LINK_HEADER = "link"
private const val LINK_SEPARATOR = ","
private const val PARAM_SEPARATOR = ";"

/**
 * Extracts next page link from Github pagination header,
 * if it is available
 */
fun getNextPageLink(response: HttpResponse<String>): URI? {

    val header = response.headers().firstValue(LINK_HEADER)

    if (header.isEmpty) {
        return null
    }

    val links = header.get().split(LINK_SEPARATOR).map { it.trim() }
    val next = links.findLast { s -> s.endsWith("rel=\"next\"") }

    if (next == null) {
        return null
    }

    val link = next.split(PARAM_SEPARATOR).first()

    if (!link.startsWith("<") || !link.endsWith(">")) {
        return null
    }

    return URI.create(link.substring(1, link.length - 1))
}