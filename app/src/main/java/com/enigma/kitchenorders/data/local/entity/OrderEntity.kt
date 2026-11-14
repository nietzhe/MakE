package com.enigma.kitchenorders.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ACTIVE = placed and not fully cooked yet.
 * DONE   = fully cooked and ready to pack/deliver (NOT a payment or delivery state).
 */
enum class OrderStatus { ACTIVE, DONE }

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val createdAt: Long,
    val deliveryDate: Long,
    val status: OrderStatus = OrderStatus.ACTIVE,
    val completedAt: Long? = null,
    @ColumnInfo(defaultValue = "0") val isDeleted: Boolean = false
)
