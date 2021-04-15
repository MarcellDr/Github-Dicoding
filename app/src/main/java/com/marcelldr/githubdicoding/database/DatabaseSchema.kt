package com.marcelldr.githubdicoding.database

import android.net.Uri
import android.provider.BaseColumns

class DatabaseSchema {
    companion object {
        const val AUTHORITY = "com.marcelldr.githubdicoding"
        const val SCHEME = "content"
    }

    class FavoriteTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite"
            const val KEY_USERNAME = "username"
            const val KEY_NAME = "name"
            const val KEY_AVATAR = "avatar"
            const val KEY_COMPANY = "company"
            const val KEY_LOCATION = "location"
            const val KEY_REPO = "repo"
            const val KEY_FOLLOWER = "follower"
            const val KEY_FOLLOWING = "following"
            const val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                    "($KEY_USERNAME TEXT PRIMARY KEY NOT NULL," +
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
        companion object {
            const val TABLE_NAME = "setting"
            const val KEY_ID = "id"
            const val KEY_ALARM = "alarm"
            const val CREATE_TABLE = "CREATE TABLE $TABLE_NAME " +
                    "($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$KEY_ALARM TEXT NOT NULL)"
            const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        }
    }
}