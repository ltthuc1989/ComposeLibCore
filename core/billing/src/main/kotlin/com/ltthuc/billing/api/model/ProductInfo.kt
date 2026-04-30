package com.ltthuc.billing.api.model

data class ProductInfo(
    val productId: String,
    val title: String,
    val formattedPrice: String,
    val priceMicros: Long,
    val currencyCode: String,
)
