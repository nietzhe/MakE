package com.enigma.kitchenorders.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.enigma.kitchenorders.data.local.entity.OrderEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(parentColumn = "id", entityColumn = "orderId")
    val items: List<OrderItemEntity>
) {
    val totalQuantity: Int get() = items.sumOf { it.quantity }
}
