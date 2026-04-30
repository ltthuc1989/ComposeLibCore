package com.ltthuc.network.impl.api

import com.ltthuc.network.api.SampleApi
import com.ltthuc.network.api.SampleItemDto
import com.ltthuc.network.impl.ApiService
import javax.inject.Inject

internal class DefaultSampleApi @Inject constructor(
    private val apiService: ApiService,
) : SampleApi {

    override suspend fun getItems(): List<SampleItemDto> = apiService.getItems()
}
