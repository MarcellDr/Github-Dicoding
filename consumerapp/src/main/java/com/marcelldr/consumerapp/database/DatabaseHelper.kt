package com.marcelldr.consumerapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {
    companion object {
        const val DATABASE_NAME = "github_db"
        const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DatabaseSchema.FavoriteTable.CREATE_TABLE)
        db?.execSQL(DatabaseSchema.SettingTable.CREATE_TABLE)
        db?.execSQL(
            "INSERT INTO ${DatabaseSchema.SettingTable.TABLE_NAME} " +
                    "(${DatabaseSchema.SettingTable._ID}, ${DatabaseSchema.SettingTable.KEY_ALARM}) " +
                    "VALUES (1, \"09:00\")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DatabaseSchema.FavoriteTable.DROP_TABLE)
        db?.execSQL(DatabaseSchema.SettingTable.DROP_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}