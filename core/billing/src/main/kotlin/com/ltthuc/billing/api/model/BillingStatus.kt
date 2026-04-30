package com.ltthuc.billing.api.model

sealed interface BillingStatus {
    object Disconnected : BillingStatus
    object Connecting : BillingStatus
    object Ready : BillingStatus
    data class Error(val code: Int, val message: String) : BillingStatus
}
