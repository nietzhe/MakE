package com.enigma.kitchenorders.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.core.app.ApplicationProvider
import com.enigma.kitchenorders.data.local.AppDatabase
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import com.enigma.kitchenorders.data.local.entity.OrderStatus
import com.enigma.kitchenorders.data.local.model.OrderWithItems
import com.enigma.kitchenorders.data.local.model.ProductionTotal
import com.enigma.kitchenorders.data.repository.KitchenRepositoryImpl
import com.enigma.kitchenorders.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OrderRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: KitchenRepositoryImpl

    private var clock = 1_000L
    private val today = DateUtils.startOfDay()
    private val tomorrow = DateUtils.startOfDay(1)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = KitchenRepositoryImpl(db) { clock++ }
        runBlocking {
            db.menuDao().insertMenu(MenuEntity(id = 1, name = AYAM, currentPrice = 20_000.0))
            db.menuDao().insertMenu(MenuEntity(id = 2, name = NASI, currentPrice = 5_000.0))
            db.menuDao().insertMenu(MenuEntity(id = 3, name = TEH, currentPrice = 5_000.0))
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    // --- Production totals -------------------------------------------------------------------

    @Test
    fun totals_exclude_done_orders() = runBlocking {
        placeOrder("Budi", today, ayam(5), nasi(2))
        placeOrder("Sari", today, ayam(3), teh(4))

        repo.markDone(orderId("Budi"))

        assertEquals(
            listOf(ProductionTotal(TEH, 4), ProductionTotal(AYAM, 3)),
            repo.observeProductionTotals(today).first()
        )
    }

    @Test
    fun totals_exclude_deleted_orders_and_include_them_again_after_undo() = runBlocking {
        placeOrder("Budi", today, ayam(5))
        placeOrder("Sari", today, ayam(3), nasi(1))

        val sari = orderId("Sari")
        repo.softDelete(sari)
        assertEquals(listOf(ProductionTotal(AYAM, 5)), repo.observeProductionTotals(today).first())

        repo.undoDelete(sari)
        assertEquals(
            listOf(ProductionTotal(AYAM, 8), ProductionTotal(NASI, 1)),
            repo.observeProductionTotals(today).first()
        )
    }

    @Test
    fun totals_sum_across_orders_and_sort_by_quantity_descending() = runBlocking {
        placeOrder("Budi", today, nasi(2), ayam(5))
        placeOrder("Sari", today, ayam(3), nasi(1), teh(10))
        placeOrder("Ani", today, ayam(12))

        assertEquals(
            listOf(ProductionTotal(AYAM, 20), ProductionTotal(TEH, 10), ProductionTotal(NASI, 3)),
            repo.observeProductionTotals(today).first()
        )
    }

    @Test
    fun totals_only_cover_the_selected_delivery_date() = runBlocking {
        placeOrder("Budi", today, ayam(5))
        placeOrder("Sari", tomorrow, ayam(7))

        assertEquals(listOf(ProductionTotal(AYAM, 5)), repo.observeProductionTotals(today).first())
        assertEquals(listOf(ProductionTotal(AYAM, 7)), repo.observeProductionTotals(tomorrow).first())
    }

    @Test
    fun totals_carry_no_customer_information() = runBlocking {
        placeOrder("Budi Santoso", today, ayam(5))

        val totals = repo.observeProductionTotals(today).first()

        assertTrue(totals.none { it.toString().contains("Budi") })
    }

    @Test
    fun reopened_order_counts_towards_totals_again() = runBlocking {
        placeOrder("Budi", today, ayam(5))
        val id = orderId("Budi")

        repo.markDone(id)
        assertTrue(repo.observeProductionTotals(today).first().isEmpty())

        repo.reopen(id)
        assertEquals(listOf(ProductionTotal(AYAM, 5)), repo.observeProductionTotals(today).first())
    }

    @Test
    fun done_quantity_counts_done_orders_only_and_skips_deleted_ones() = runBlocking {
        placeOrder("Budi", today, ayam(5), nasi(2))
        placeOrder("Sari", today, ayam(3))
        placeOrder("Ani", today, teh(4))

        assertEquals(0, repo.observeDoneQuantity(today).first())

        repo.markDone(orderId("Budi"))
        repo.markDone(orderId("Sari"))
        repo.markDone(orderId("Ani"))
        repo.softDelete(orderId("Ani"))

        assertEquals(10, repo.observeDoneQuantity(today).first())
    }

    // --- Order lifecycle ---------------------------------------------------------------------

    @Test
    fun markDone_stores_completion_time_and_reopen_clears_it() = runBlocking {
        placeOrder("Budi", today, ayam(5))
        val id = orderId("Budi")

        repo.markDone(id)
        val done = db.orderDao().getOrderById(id)!!
        assertEquals(OrderStatus.DONE, done.status)
        assertNotNull(done.completedAt)

        repo.reopen(id)
        val reopened = db.orderDao().getOrderById(id)!!
        assertEquals(OrderStatus.ACTIVE, reopened.status)
        assertNull(reopened.completedAt)
    }

    @Test
    fun orders_list_includes_done_orders_and_excludes_deleted_ones() = runBlocking {
        placeOrder("Budi", today, ayam(5))
        placeOrder("Sari", today, ayam(3))
        placeOrder("Ani", today, ayam(1))

        repo.markDone(orderId("Sari"))
        repo.softDelete(orderId("Ani"))

        val orders = repo.observeOrdersByDate(today).first()
        assertEquals(setOf("Budi", "Sari"), orders.map { it.order.customerName }.toSet())
        assertEquals(OrderStatus.DONE, orders.first { it.order.customerName == "Sari" }.order.status)
    }

    @Test
    fun editing_an_order_updates_the_same_record_and_does_not_duplicate_it() = runBlocking {
        placeOrder("Budi", today, ayam(5), nasi(2))
        val original = ordersOn(today).single()

        val saved = repo.updateOrder(
            original.order.copy(customerName = "Budi S", deliveryDate = tomorrow),
            listOf(ayam(8))
        )

        assertTrue(saved)
        assertTrue(ordersOn(today).isEmpty())
        val updated = ordersOn(tomorrow).single()
        assertEquals(original.order.id, updated.order.id)
        assertEquals("Budi S", updated.order.customerName)
        assertEquals(original.order.createdAt, updated.order.createdAt)
        assertEquals(OrderStatus.ACTIVE, updated.order.status)
        assertEquals(listOf(AYAM to 8), updated.items.map { it.menuNameSnapshot to it.quantity })
        assertEquals(1, rowCount("orders"))
        assertEquals(1, rowCount("order_items"))
    }

    @Test
    fun done_orders_cannot_be_edited() = runBlocking {
        placeOrder("Budi", today, ayam(5))
        val id = orderId("Budi")
        repo.markDone(id)
        val done = ordersOn(today).single()

        val saved = repo.updateOrder(done.order.copy(customerName = "Hacker"), listOf(ayam(99)))

        assertFalse(saved)
        val after = ordersOn(today).single()
        assertEquals("Budi", after.order.customerName)
        assertEquals(listOf(AYAM to 5), after.items.map { it.menuNameSnapshot to it.quantity })
    }

    @Test
    fun purge_removes_only_soft_deleted_orders_together_with_their_items() = runBlocking {
        placeOrder("Budi", today, ayam(5), nasi(2))
        placeOrder("Sari", today, ayam(3))
        repo.softDelete(orderId("Sari"))

        repo.purgeDeletedOrders()

        assertEquals(1, rowCount("orders"))
        assertEquals(2, rowCount("order_items"))
        assertEquals("Budi", ordersOn(today).single().order.customerName)
    }

    // --- Helpers -----------------------------------------------------------------------------

    private suspend fun placeOrder(name: String, date: Long, vararg items: OrderItemEntity) {
        repo.createOrder(name, date, items.toList())
    }

    private suspend fun ordersOn(date: Long): List<OrderWithItems> = repo.observeOrdersByDate(date).first()

    private suspend fun orderId(name: String): Long =
        listOf(today, tomorrow).flatMap { ordersOn(it) }.first { it.order.customerName == name }.order.id

    private fun rowCount(table: String): Int =
        db.query(SimpleSQLiteQuery("SELECT COUNT(*) FROM $table")).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

    private fun ayam(qty: Int) = item(1, AYAM, qty)
    private fun nasi(qty: Int) = item(2, NASI, qty)
    private fun teh(qty: Int) = item(3, TEH, qty)

    private fun item(menuId: Int, name: String, qty: Int) = OrderItemEntity(
        orderId = 0,
        menuId = menuId,
        menuNameSnapshot = name,
        quantity = qty,
        priceSnapshot = 1.0,
        notes = null
    )

    private companion object {
        const val AYAM = "Ayam Goreng"
        const val NASI = "Nasi Putih"
        const val TEH = "Es Teh Manis"
    }
}
