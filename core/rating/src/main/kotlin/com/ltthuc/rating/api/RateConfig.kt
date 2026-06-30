package com.ltthuc.rating.api

data class RateConfig(
    val key: String? = null,
    val numberOfTimes: Int = 3,
    val autoIncreaseCount: Boolean = false,
)
