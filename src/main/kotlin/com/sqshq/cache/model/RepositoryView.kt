package com.sqshq.cache.model

/**
 * Data structure to store calculated and sorted views for repository
 */
data class RepositoryView(var byForks: List<RepositoryData<Int>> = emptyList(),
                          var byStars: List<RepositoryData<Int>> = emptyList(),
                          var byOpenIssues: List<RepositoryData<Int>> = emptyList(),
                          var byLastUpdatedTime: List<RepositoryData<String>> = emptyList())

/**
 * Data structure which represents repository name and corresponding metadata
 * [toString] returns valid json array string
 */
data class RepositoryData<T>(val name: String, val data: T) {
    override fun toString(): String = "[\"$name\", ${if (data is String) "\"$data\"" else "$data"}]"
}