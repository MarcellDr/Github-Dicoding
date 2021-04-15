package com.marcelldr.githubdicoding.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class DatabaseHandler(context: Context) {
    private lateinit var database: SQLiteDatabase
    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private val INSTANCE: DatabaseHandler? = null
        fun getInstance(context: Context): DatabaseHandler = INSTANCE ?: synchronized(this) {
            INSTANCE ?: DatabaseHandler(context)
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()

        if (database.isOpen) {
            database.close()
        }
    }

    fun getAll(table: String): Cursor {
        return database.query(
            table,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    fun insert(table: String, values: ContentValues): Long {
        return database.insert(table, null, values)
    }

    fun update(table: String, where: String, oldValue: String, newValue: ContentValues): Int {
        return database.update(table, newValue, "$where = ?", arrayOf(oldValue))
    }

    fun delete(table: String, where: String, value: String?): Int {
        return database.delete(table, "$where = ?", arrayOf(value))
    }

    fun where(table: String, where: String, value: String?): Cursor {
        return database.query(
            table,
            null,
            "$where = ?",
            arrayOf(value),
            null,
            null,
            null,
            null
        )
    }
}