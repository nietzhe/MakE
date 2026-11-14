package com.enigma.kitchenorders.data.repository

import com.enigma.kitchenorders.data.local.AppDatabase
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import com.enigma.kitchenorders.domain.repository.KitchenRepository
import kotlinx.coroutines.flow.Flow

class KitchenRepositoryImpl(
    private val db: AppDatabase,
    private val clock: () -> Long = System::currentTimeMillis
) : KitchenRepository {

    override fun getAllMenus(): Flow<List<MenuEntity>> = db.menuDao().getAllMenus()

    override fun getActiveMenus(): Flow<List<MenuEntity>> = db.menuDao().getAllActiveMenus()

    override suspend fun saveMenu(menu: MenuEntity) = db.menuDao().insertMenu(menu)

    override suspend fun deleteMenu(menu: MenuEntity) = db.menuDao().deleteMenu(menu)

    override suspend fun createOrder(customerName: String, deliveryDate: Long, items: List<OrderItemEntity>) {
        val order = OrderEntity(
            customerName = customerName,
            createdAt = clock(),
            deliveryDate = deliveryDate
        )
        db.orderDao().placeOrder(order, items)
    }

    override fun observeOrdersByDate(date: Long): Flow<List<OrderWithItems>> =
        db.orderDao().observeOrdersByDate(date)

    override fun observeProductionTotals(date: Long): Flow<List<ProductionTotal>> =
        db.orderDao().observeProductionTotals(date)

    override fun observeDoneQuantity(date: Long): Flow<Int> =
        db.orderDao().observeDoneQuantity(date)

    override suspend fun markDone(id: Long) {
        db.orderDao().markDone(id, clock())
    }

    override suspend fun reopen(id: Long) {
        db.orderDao().reopen(id)
    }

    override suspend fun softDelete(id: Long) = db.orderDao().softDelete(id)

    override suspend fun undoDelete(id: Long) = db.orderDao().undoDelete(id)

    override suspend fun updateOrder(order: OrderEntity, items: List<OrderItemEntity>): Boolean =
        db.orderDao().updateOrderWithItems(order, items)

    override suspend fun purgeDeletedOrders() {
        db.orderDao().purgeDeleted()
    }
}
