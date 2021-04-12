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
            val KEY_NAME = "name"
            val KEY_AVATAR = "avatar"
            val KEY_COMPANY = "company"
            val KEY_LOCATION = "location"
            val KEY_REPO = "repo"
            val KEY_FOLLOWER = "follower"
            val KEY_FOLLOWING = "following"
            val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                    "(${this._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_USERNAME TEXT NOT NULL," +
                    "$KEY_NAME TEXT NOT NULL," +
                    "$KEY_AVATAR TEXT NOT NULL," +
                    "$KEY_COMPANY TEXT NOT NULL," +
                    "$KEY_LOCATION TEXT NOT NULL," +
                    "$KEY_REPO INTEGER NOT NULL," +
                    "$KEY_FOLLOWER INTEGER NOT NULL," +
                    "$KEY_FOLLOWING INTEGER NOT NULL)"
            val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}