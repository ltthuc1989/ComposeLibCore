package com.dvm.database.api.models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "order_item",
    primaryKeys = ["dishId", "orderId"],
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItem(
    val name: String,
    val orderId: String,
    val image: String,
    val amount: Int,
    val price: Int,
    val dishId: String
)