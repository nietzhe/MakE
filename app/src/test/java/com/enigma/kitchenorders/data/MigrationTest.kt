package com.enigma.kitchenorders.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.enigma.kitchenorders.data.local.AppDatabase
import com.enigma.kitchenorders.data.local.MIGRATION_1_2
import com.enigma.kitchenorders.data.local.entity.OrderStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Builds a real version-1 database (the schema shipped before the kitchen-flow change), fills it
 * with business data, then opens it through Room with [MIGRATION_1_2]. Room validates the migrated
 * schema against the v2 entities on open, so a wrong migration fails here too.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MigrationTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var db: AppDatabase? = null

    @Before
    fun setUp() {
        context.deleteDatabase(DB_NAME)
    }

    @After
    fun tearDown() {
        db?.close()
        context.deleteDatabase(DB_NAME)
    }

    @Test
    fun migration_1_to_2_preserves_every_existing_row() = runBlocking {
        createVersion1Database()

        val database = openMigrated()

        val menus = database.menuDao().getAllMenus().first()
        assertEquals(listOf("Ayam Goreng", "Nasi Putih"), menus.map { it.name })

        val orders = database.orderDao().observeOrdersByDate(DELIVERY).first()
        assertEquals(listOf("Sari", "Budi"), orders.map { it.order.customerName }) // newest first
        val budi = orders.first { it.order.customerName == "Budi" }
        assertEquals(1L, budi.order.id)
        assertEquals(1_000L, budi.order.createdAt)
        assertEquals(DELIVERY, budi.order.deliveryDate)
        assertEquals(listOf("Ayam Goreng" to 5, "Nasi Putih" to 2), budi.items.map { it.menuNameSnapshot to it.quantity })
        assertEquals("pedas", budi.items.first().notes)
        assertEquals(20_000.0, budi.items.first().priceSnapshot, 0.0)
    }

    @Test
    fun migrated_orders_are_active_and_not_deleted() = runBlocking {
        createVersion1Database()

        val database = openMigrated()

        database.orderDao().observeOrdersByDate(DELIVERY).first().forEach { row ->
            assertEquals(OrderStatus.ACTIVE, row.order.status)
            assertFalse(row.order.isDeleted)
            assertNull(row.order.completedAt)
        }
        // And they feed production totals right away.
        val totals = database.orderDao().observeProductionTotals(DELIVERY).first()
        assertEquals(listOf("Ayam Goreng" to 8, "Nasi Putih" to 2), totals.map { it.menuName to it.totalQuantity })
    }

    @Test
    fun legacy_cancelled_orders_become_soft_deleted_instead_of_active() = runBlocking {
        createVersion1Database()

        val database = openMigrated()

        // The cancelled order is hidden everywhere but is not destroyed by the migration itself.
        val visible = database.orderDao().observeOrdersByDate(DELIVERY).first()
        assertTrue(visible.none { it.order.customerName == "Dibatalkan" })
        val cancelled = database.orderDao().getOrderById(3)!!
        assertTrue(cancelled.isDeleted)
        assertEquals(OrderStatus.ACTIVE, cancelled.status)
    }

    private fun openMigrated(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .addMigrations(MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
            .also { db = it }

    /** Mirrors the Room-generated v1 schema exactly (no completedAt / isDeleted columns). */
    private fun createVersion1Database() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(DB_NAME)
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE IF NOT EXISTS `menus` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `currentPrice` REAL NOT NULL, `isActive` INTEGER NOT NULL)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `orders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `customerName` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `deliveryDate` INTEGER NOT NULL, `status` TEXT NOT NULL)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS `order_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `orderId` INTEGER NOT NULL, `menuId` INTEGER NOT NULL, `menuNameSnapshot` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `priceSnapshot` REAL NOT NULL, `notes` TEXT, FOREIGN KEY(`orderId`) REFERENCES `orders`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`menuId`) REFERENCES `menus`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_order_items_orderId` ON `order_items` (`orderId`)")
                        db.execSQL("CREATE INDEX IF NOT EXISTS `index_order_items_menuId` ON `order_items` (`menuId`)")
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
                })
                .build()
        )
        helper.writableDatabase.use { db ->
            db.execSQL("INSERT INTO menus (id, name, currentPrice, isActive) VALUES (1, 'Ayam Goreng', 20000.0, 1), (2, 'Nasi Putih', 5000.0, 1)")
            db.execSQL("INSERT INTO orders (id, customerName, createdAt, deliveryDate, status) VALUES (1, 'Budi', 1000, $DELIVERY, 'ACTIVE')")
            db.execSQL("INSERT INTO orders (id, customerName, createdAt, deliveryDate, status) VALUES (2, 'Sari', 2000, $DELIVERY, 'ACTIVE')")
            db.execSQL("INSERT INTO orders (id, customerName, createdAt, deliveryDate, status) VALUES (3, 'Dibatalkan', 3000, $DELIVERY, 'CANCELLED')")
            db.execSQL("INSERT INTO order_items (orderId, menuId, menuNameSnapshot, quantity, priceSnapshot, notes) VALUES (1, 1, 'Ayam Goreng', 5, 20000.0, 'pedas')")
            db.execSQL("INSERT INTO order_items (orderId, menuId, menuNameSnapshot, quantity, priceSnapshot, notes) VALUES (1, 2, 'Nasi Putih', 2, 5000.0, NULL)")
            db.execSQL("INSERT INTO order_items (orderId, menuId, menuNameSnapshot, quantity, priceSnapshot, notes) VALUES (2, 1, 'Ayam Goreng', 3, 20000.0, NULL)")
            db.execSQL("INSERT INTO order_items (orderId, menuId, menuNameSnapshot, quantity, priceSnapshot, notes) VALUES (3, 1, 'Ayam Goreng', 9, 20000.0, NULL)")
        }
    }

    private companion object {
        const val DB_NAME = "migration-test.db"
        const val DELIVERY = 1_700_000_000_000L
    }
}
