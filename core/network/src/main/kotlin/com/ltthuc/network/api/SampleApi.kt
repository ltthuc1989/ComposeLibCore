package com.ltthuc.network.api

interface SampleApi {
    suspend fun getItems(): List<SampleItemDto>
}

data class SampleItemDto(
    val id: String,
    val title: String,
    val subtitle: String,
)
