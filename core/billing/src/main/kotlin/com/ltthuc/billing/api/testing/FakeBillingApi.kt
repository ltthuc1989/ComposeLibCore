package com.ltthuc.billing.api.testing

import android.app.Activity
import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.api.model.BillingStatus
import com.ltthuc.billing.api.model.ProductInfo
import com.ltthuc.billing.api.model.PurchaseInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [BillingApi] for unit tests.
 *
 * Drop into ViewModel tests directly, or replace via Hilt `@TestInstallIn` for integration tests.
 * Not used in production — kept in `main/` source set so consumer modules can access it without
 * requiring `java-test-fixtures` plugin or a separate test artifact.
 */
class FakeBillingApi : BillingApi {

    val statusFlow = MutableStateFlow<BillingStatus>(BillingStatus.Ready)
    val purchasesFlow = MutableStateFlow<List<PurchaseInfo>>(emptyList())

    override val status: StateFlow<BillingStatus> = statusFlow.asStateFlow()
    override val purchases: StateFlow<List<PurchaseInfo>> = purchasesFlow.asStateFlow()

    var queryProductsResult: Result<List<ProductInfo>> = Result.success(emptyList())
    var queryPurchasesResult: Result<List<PurchaseInfo>> = Result.success(emptyList())
    var launchPurchaseResult: Result<Unit> = Result.success(Unit)
    var acknowledgeResult: Result<Unit> = Result.success(Unit)

    val launchPurchaseInvocations: MutableList<Pair<Activity, String>> = mutableListOf()
    val acknowledgeInvocations: MutableList<String> = mutableListOf()
    val queryProductsInvocations: MutableList<List<String>> = mutableListOf()
    var queryPurchasesCount: Int = 0
    var connectCount: Int = 0
    var releaseCount: Int = 0

    override fun connect() {
        connectCount++
    }

    override fun release() {
        releaseCount++
    }

    override suspend fun queryProducts(productIds: List<String>): Result<List<ProductInfo>> {
        queryProductsInvocations += productIds
        return queryProductsResult
    }

    override suspend fun queryPurchases(): Result<List<PurchaseInfo>> {
        queryPurchasesCount++
        queryPurchasesResult.onSuccess { purchasesFlow.value = it }
        return queryPurchasesResult
    }

    override suspend fun launchPurchase(activity: Activity, productId: String): Result<Unit> {
        launchPurchaseInvocations += activity to productId
        return launchPurchaseResult
    }

    override suspend fun acknowledge(purchaseToken: String): Result<Unit> {
        acknowledgeInvocations += purchaseToken
        return acknowledgeResult
    }
}
