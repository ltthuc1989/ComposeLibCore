package com.ltthuc.billing.impl

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.ltthuc.billing.api.model.ProductInfo
import com.ltthuc.billing.api.model.PurchaseInfo

internal fun ProductDetails.toInfo(): ProductInfo {
    val price = oneTimePurchaseOfferDetails
    return ProductInfo(
        productId = productId,
        title = title,
        formattedPrice = price?.formattedPrice.orEmpty(),
        priceMicros = price?.priceAmountMicros ?: 0L,
        currencyCode = price?.priceCurrencyCode.orEmpty(),
    )
}

internal fun Purchase.toInfo(): List<PurchaseInfo> = products.map { productId ->
    PurchaseInfo(
        productId = productId,
        purchaseToken = purchaseToken,
        isAcknowledged = isAcknowledged,
        purchaseTime = purchaseTime,
    )
}
