package com.ltthuc.billing.api.model

sealed interface BillingError {
    object NotConnected : BillingError
    object ProductNotFound : BillingError
    object UserCanceled : BillingError
    data class Unknown(val code: Int, val message: String) : BillingError
}
