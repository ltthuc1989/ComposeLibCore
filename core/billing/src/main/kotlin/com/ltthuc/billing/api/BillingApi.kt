package com.ltthuc.billing.api

import android.app.Activity
import com.ltthuc.billing.api.model.BillingStatus
import com.ltthuc.billing.api.model.ProductInfo
import com.ltthuc.billing.api.model.PurchaseInfo
import kotlinx.coroutines.flow.StateFlow

interface BillingApi {

    val status: StateFlow<BillingStatus>
    val purchases: StateFlow<List<PurchaseInfo>>

    fun connect()
    fun release()

    suspend fun queryProducts(productIds: List<String>): Result<List<ProductInfo>>
    suspend fun queryPurchases(): Result<List<PurchaseInfo>>
    suspend fun launchPurchase(activity: Activity, productId: String): Result<Unit>
    suspend fun acknowledge(purchaseToken: String): Result<Unit>
}
