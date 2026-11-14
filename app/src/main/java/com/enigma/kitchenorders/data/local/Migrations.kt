package com.enigma.kitchenorders.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 -> v2: adds kitchen-flow columns to `orders`.
 *
 * - `completedAt` (nullable) and `isDeleted` (default 0) are added; every existing order stays ACTIVE.
 * - The legacy, never-surfaced 'CANCELLED' status is folded into the new soft-delete flag.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `orders` ADD COLUMN `completedAt` INTEGER")
        db.execSQL("ALTER TABLE `orders` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE `orders` SET `isDeleted` = 1, `status` = 'ACTIVE' WHERE `status` = 'CANCELLED'")
    }
}
