package com.sqshq.cache.controller

import com.sqshq.cache.client.ApiClient
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.http.HttpClient

@RestController
class HealthcheckController(private val client: ApiClient) {

    @GetMapping("/healthcheck")
    fun healthcheck() : ResponseEntity<HttpStatus> {
        return if (client.healthcheck()) {
            ResponseEntity(OK)
        } else {
            ResponseEntity(SERVICE_UNAVAILABLE)
        }
    }
}