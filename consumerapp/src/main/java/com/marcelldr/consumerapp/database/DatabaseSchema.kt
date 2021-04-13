package com.marcelldr.consumerapp.database

import android.net.Uri
import android.provider.BaseColumns

open class KBaseColumns {
    val _ID = "_id"
}

class DatabaseSchema {
    companion object {
        const val AUTHORITY = "com.marcelldr.githubdicoding"
        const val SCHEME = "content"
    }

    class FavoriteTable : BaseColumns {
        companion object : KBaseColumns() {
            const val TABLE_NAME = "favorite"
            const val KEY_USERNAME = "username"
            const val KEY_NAME = "name"
            const val KEY_AVATAR = "avatar"
            const val KEY_COMPANY = "company"
            const val KEY_LOCATION = "location"
            const val KEY_REPO = "repo"
            const val KEY_FOLLOWER = "follower"
            const val KEY_FOLLOWING = "following"
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
            const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }

    class SettingTable : BaseColumns {
        companion object : KBaseColumns() {
            const val TABLE_NAME = "setting"
            const val KEY_ALARM = "alarm"
            val CREATE_TABLE = "CREATE TABLE $TABLE_NAME" +
                    "(${this._ID} INTEGER PRIMARY KEY," +
                    "$KEY_ALARM TEXT NOT NULL)"
            const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}