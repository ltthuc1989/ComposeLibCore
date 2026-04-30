package com.ltthuc.network.impl

import com.ltthuc.network.api.SampleItemDto
import retrofit2.http.GET

internal interface ApiService {

    @GET("posts")
    suspend fun getItems(): List<SampleItemDto>

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}
