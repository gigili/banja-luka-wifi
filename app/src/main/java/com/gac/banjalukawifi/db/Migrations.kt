package com.gac.banjalukawifi.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

abstract class Migrations {
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS networks;")
                db.execSQL("CREATE TABLE IF NOT EXISTS networks( id INTEGER NOT NULL, name TEXT NOT NULL, password TEXT NOT NULL, address TEXT, geoLat TEXT, geoLong TEXT, userID TEXT, PRIMARY KEY('id'));")
                db.execSQL("DROP TABLE IF EXISTS user_data;")
            }
        }
    }
}