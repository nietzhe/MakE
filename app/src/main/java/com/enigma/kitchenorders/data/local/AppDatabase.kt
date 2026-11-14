package com.enigma.kitchenorders.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.enigma.kitchenorders.data.local.dao.MenuDao
import com.enigma.kitchenorders.data.local.dao.OrderDao
import com.enigma.kitchenorders.data.local.entity.MenuEntity
import com.enigma.kitchenorders.data.local.entity.OrderEntity
import com.enigma.kitchenorders.data.local.entity.OrderItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MenuEntity::class, OrderEntity::class, OrderItemEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitchen_orders.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.menuDao())
                    }
                }
            }

            private suspend fun populateDatabase(menuDao: MenuDao) {
                // samples
                val sampleMenus = listOf(
                    MenuEntity(name = "Nasi Goreng", currentPrice = 15000.0),
                    MenuEntity(name = "Mie Goreng", currentPrice = 15000.0),
                    MenuEntity(name = "Ayam Goreng", currentPrice = 20000.0),
                    MenuEntity(name = "Ayam Bakar", currentPrice = 22000.0),
                    MenuEntity(name = "Nasi Putih", currentPrice = 5000.0),
                    MenuEntity(name = "Es Teh Manis", currentPrice = 5000.0),
                    MenuEntity(name = "Es Jeruk", currentPrice = 7000.0),
                    MenuEntity(name = "Soto Ayam", currentPrice = 18000.0),
                    MenuEntity(name = "Gado-gado", currentPrice = 15000.0),
                    MenuEntity(name = "Sate Ayam", currentPrice = 20000.0)
                )
                sampleMenus.forEach { menuDao.insertMenu(it) }
            }
        }
    }
}
