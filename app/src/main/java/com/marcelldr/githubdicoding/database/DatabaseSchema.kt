package com.marcelldr.githubdicoding.database

import android.provider.BaseColumns

class DatabaseSchema {
    class FavoriteTable: BaseColumns {
        companion object {
            val TABLE_NAME = "favorite"
            val KEY_ID = "_id"
            val KEY_USERNAME = "username"
            val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                    "($KEY_ID INTEGER PRIMARY KEY," +
                    "$KEY_USERNAME TEXT)"
            val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}