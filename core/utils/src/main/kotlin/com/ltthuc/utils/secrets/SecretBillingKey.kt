package com.ltthuc.utils.secrets

interface ISecretBillingKey {
    val licenseKey: String
    val productIds: List<String>
    val subscriptionIds: List<String>
}
