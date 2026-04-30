package com.ltthuc.billing.api.model

data class PurchaseInfo(
    val productId: String,
    val purchaseToken: String,
    val isAcknowledged: Boolean,
    val purchaseTime: Long,
)
