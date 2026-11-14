package com.enigma.kitchenorders.data.local.dao

import androidx.room.*
import com.enigma.kitchenorders.data.local.entity.OrderEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>): Long {
        val orderId = insertOrder(order)
        insertOrderItems(items.map { it.copy(id = 0, orderId = orderId) })
        return orderId
    }

    /** All non-deleted orders (ACTIVE and DONE) for a delivery date, newest first. */
    @Transaction
    @Query("SELECT * FROM orders WHERE deliveryDate = :deliveryDate AND isDeleted = 0 ORDER BY createdAt DESC, id DESC")
    fun observeOrdersByDate(deliveryDate: Long): Flow<List<OrderWithItems>>

    /** What is still left to cook: ACTIVE, non-deleted orders only, summed per menu name. */
    @Query(
        """
        SELECT oi.menuNameSnapshot AS menuName, SUM(oi.quantity) AS totalQuantity
        FROM order_items oi
        INNER JOIN orders o ON o.id = oi.orderId
        WHERE o.deliveryDate = :deliveryDate AND o.status = 'ACTIVE' AND o.isDeleted = 0
        GROUP BY oi.menuNameSnapshot
        ORDER BY totalQuantity DESC, menuName ASC
        """
    )
    fun observeProductionTotals(deliveryDate: Long): Flow<List<ProductionTotal>>

    /** Portions already cooked (DONE, non-deleted orders) for a delivery date. */
    @Query(
        """
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM order_items oi
        INNER JOIN orders o ON o.id = oi.orderId
        WHERE o.deliveryDate = :deliveryDate AND o.status = 'DONE' AND o.isDeleted = 0
        """
    )
    fun observeDoneQuantity(deliveryDate: Long): Flow<Int>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): OrderEntity?

    @Query("UPDATE orders SET status = 'DONE', completedAt = :completedAt WHERE id = :id AND status = 'ACTIVE' AND isDeleted = 0")
    suspend fun markDone(id: Long, completedAt: Long): Int

    @Query("UPDATE orders SET status = 'ACTIVE', completedAt = NULL WHERE id = :id AND status = 'DONE' AND isDeleted = 0")
    suspend fun reopen(id: Long): Int

    @Query("UPDATE orders SET isDeleted = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE orders SET isDeleted = 0 WHERE id = :id")
    suspend fun undoDelete(id: Long)

    /** Permanently removes soft-deleted orders (their items cascade). */
    @Query("DELETE FROM orders WHERE isDeleted = 1")
    suspend fun purgeDeleted(): Int

    /** Only touches editable fields, and only for ACTIVE orders, so DONE orders stay locked. */
    @Query("UPDATE orders SET customerName = :customerName, deliveryDate = :deliveryDate WHERE id = :id AND status = 'ACTIVE' AND isDeleted = 0")
    suspend fun updateEditableFields(id: Long, customerName: String, deliveryDate: Long): Int

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteItemsByOrder(orderId: Long)

    /** Updates the existing record in place (never inserts a new order). Returns false if not editable. */
    @Transaction
    suspend fun updateOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>): Boolean {
        if (updateEditableFields(order.id, order.customerName, order.deliveryDate) == 0) return false
        deleteItemsByOrder(order.id)
        insertOrderItems(items.map { it.copy(id = 0, orderId = order.id) })
        return true
    }
}
