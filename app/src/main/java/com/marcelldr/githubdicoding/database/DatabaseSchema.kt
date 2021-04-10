package com.marcelldr.githubdicoding.database

import android.provider.BaseColumns

open class KBaseColumns {
    val _ID = "_id"
}

class DatabaseSchema {
    class FavoriteTable: BaseColumns {
        companion object: KBaseColumns() {
            val TABLE_NAME = "favorite"
            val KEY_USERNAME = "username"
            val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                    "(${this._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_USERNAME TEXT NOT NULL)"
            val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}