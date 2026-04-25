package com.dvm.order.ordering.model

import java.io.Serializable

internal data class OrderingFields(
    val entrance: String = "",
    val floor: String = "",
    val apartment: String = "",
    val intercom: String = "",
    val comment: String = "",
) : Serializable
