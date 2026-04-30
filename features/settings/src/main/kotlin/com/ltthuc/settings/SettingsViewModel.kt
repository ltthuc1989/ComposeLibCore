package com.ltthuc.settings

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ltthuc.billing.api.BillingApi
import com.ltthuc.billing.api.OrdersID
import com.ltthuc.billing.api.model.BillingStatus
import com.ltthuc.billing.api.model.ProductInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val billingApi: BillingApi,
) : ViewModel() {

    val isPurchased: StateFlow<Boolean> = billingApi.purchases
        .map { list -> list.any { it.productId == OrdersID.REMOVE_ADS } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val productCache = MutableStateFlow<Map<String, ProductInfo>>(emptyMap())

    val removeAdsPrice: StateFlow<String> = productCache
        .map { it[OrdersID.REMOVE_ADS]?.formattedPrice.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    private val _userMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        viewModelScope.launch { loadProducts() }
    }

    private suspend fun loadProducts() {
        Log.i(TAG, "loadProducts: waiting for billing Ready")
        billingApi.status.filter { it is BillingStatus.Ready }.first()
        Log.i(TAG, "loadProducts: querying ${OrdersID.REMOVE_ADS}")
        billingApi.queryProducts(listOf(OrdersID.REMOVE_ADS))
            .onSuccess { products ->
                Log.i(TAG, "loadProducts ok count=${products.size}")
                productCache.value = products.associateBy { it.productId }
                if (products.isEmpty()) {
                    _userMessage.tryEmit(
                        "Product '${OrdersID.REMOVE_ADS}' not configured in Play Console.",
                    )
                }
            }
            .onFailure {
                Log.e(TAG, "loadProducts failed", it)
                _userMessage.tryEmit("Failed to load products: ${it.message}")
            }
    }

    fun removeAds(activity: Activity) {
        Log.i(TAG, "removeAds: clicked")
        viewModelScope.launch {
            billingApi.launchPurchase(activity, OrdersID.REMOVE_ADS)
                .onSuccess { Log.i(TAG, "removeAds: launch ok") }
                .onFailure {
                    Log.e(TAG, "removeAds failed", it)
                    _userMessage.tryEmit("Cannot remove ads: ${it.message}")
                }
        }
    }

    fun restorePurchase() {
        Log.i(TAG, "restorePurchase: clicked")
        viewModelScope.launch {
            billingApi.queryPurchases()
                .onSuccess { list ->
                    Log.i(TAG, "restorePurchase ok count=${list.size}")
                    val msg = if (list.isEmpty()) {
                        "No previous purchases found."
                    } else {
                        "Restored ${list.size} purchase(s)."
                    }
                    _userMessage.tryEmit(msg)
                }
                .onFailure {
                    Log.e(TAG, "restorePurchase failed", it)
                    _userMessage.tryEmit("Restore failed: ${it.message}")
                }
        }
    }
}
