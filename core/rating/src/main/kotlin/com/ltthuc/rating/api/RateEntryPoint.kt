package com.ltthuc.rating.api

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RateEntryPoint {
    fun rateHelper(): RateHelper
}
