package com.sqshq.cache.controller

import com.sqshq.cache.service.CacheService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class CacheController(private val service: CacheService) {

    @GetMapping("**")
    fun getCached(request: HttpServletRequest) = service.get(request.requestURI)

    @GetMapping("/view/top/{limit}/forks")
    fun getTopRepositoriesByNumberOfForks(@PathVariable("limit") limit: Int) = service.getTopRepositoriesByNumberOfForks(limit).toString()

    @GetMapping("/view/top/{limit}/last_updated")
    fun getTopRepositoriesByLastUpdatedTime(@PathVariable("limit") limit: Int) = service.getTopRepositoriesByLastUpdatedTime(limit).toString()

    @GetMapping("/view/top/{limit}/open_issues")
    fun getTopRepositoriesByOpenIssues(@PathVariable("limit") limit: Int) = service.getTopRepositoriesByOpenIssues(limit).toString()

    @GetMapping("/view/top/{limit}/stars")
    fun getTopRepositoriesByNumberStars(@PathVariable("limit") limit: Int) = service.getTopRepositoriesByNumberStars(limit).toString()

}