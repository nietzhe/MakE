package com.enigma.kitchenorders.domain.repository

import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import kotlinx.coroutines.flow.Flow

interface KitchenRepository {
    // Menu
    fun getAllMenus(): Flow<List<MenuEntity>>
    fun getActiveMenus(): Flow<List<MenuEntity>>
    suspend fun saveMenu(menu: MenuEntity)
    suspend fun deleteMenu(menu: MenuEntity)

    // Order
    suspend fun createOrder(customerName: String, deliveryDate: Long, items: List<OrderItemEntity>)

    /** ACTIVE and DONE orders (never soft-deleted ones) for a delivery date. */
    fun observeOrdersByDate(date: Long): Flow<List<OrderWithItems>>

    /** Per-menu quantities still to cook: ACTIVE, non-deleted orders only. No customer data. */
    fun observeProductionTotals(date: Long): Flow<List<ProductionTotal>>

    /** Portions already cooked (DONE, non-deleted orders) for a delivery date. */
    fun observeDoneQuantity(date: Long): Flow<Int>

    suspend fun markDone(id: Long)
    suspend fun reopen(id: Long)
    suspend fun softDelete(id: Long)
    suspend fun undoDelete(id: Long)

    /** Updates the existing order in place. Returns false when the order is DONE/deleted (not editable). */
    suspend fun updateOrder(order: OrderEntity, items: List<OrderItemEntity>): Boolean

    /** Permanently removes soft-deleted orders. Intended to run at app start. */
    suspend fun purgeDeletedOrders()
}
