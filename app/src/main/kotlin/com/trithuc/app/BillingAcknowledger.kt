package com.trithuc.app

import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.api.model.BillingStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@Singleton
class BillingAcknowledger @Inject constructor(
    private val billingApi: BillingApi,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        Timber.tag(TAG).i("start()")
        billingApi.connect()
        scope.launch {
            billingApi.status.filter { it is BillingStatus.Ready }.first()
            Timber.tag(TAG).i("status Ready -> queryPurchases")
            billingApi.queryPurchases()
        }
        billingApi.purchases
            .onEach { list ->
                Timber.tag(TAG).i("purchases emitted count=%d", list.size)
                list.filter { !it.isAcknowledged }.forEach { purchase ->
                    Timber.tag(TAG).i("acknowledging productId=%s", purchase.productId)
                    billingApi.acknowledge(purchase.purchaseToken)
                }
            }
            .launchIn(scope)
    }

    private companion object {
        const val TAG = "BillingAcknowledger"
    }
}
