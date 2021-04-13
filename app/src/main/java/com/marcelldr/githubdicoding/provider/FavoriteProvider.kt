package com.marcelldr.githubdicoding.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.marcelldr.githubdicoding.database.DatabaseHandler
import com.marcelldr.githubdicoding.database.DatabaseSchema

class FavoriteProvider : ContentProvider() {
    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private val favoriteTable = DatabaseSchema.FavoriteTable
    private lateinit var databaseHandler: DatabaseHandler

    companion object {
        const val FAVORITE = 1
        const val FAVORITE_ID = 2
    }

    init {
        sUriMatcher.addURI(
            DatabaseSchema.AUTHORITY,
            favoriteTable.TABLE_NAME,
            FAVORITE
        )
        sUriMatcher.addURI(
            DatabaseSchema.AUTHORITY,
            "${favoriteTable.TABLE_NAME}/*",
            FAVORITE_ID
        )
    }

    override fun onCreate(): Boolean {
        databaseHandler = DatabaseHandler.getInstance(context as Context)
        databaseHandler.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (sUriMatcher.match(uri)) {
            FAVORITE -> databaseHandler.getAll(favoriteTable.TABLE_NAME)
            FAVORITE_ID -> databaseHandler.where(
                favoriteTable.TABLE_NAME,
                favoriteTable.KEY_USERNAME,
                uri.lastPathSegment.toString()
            )
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (FAVORITE) {
            sUriMatcher.match(uri) -> databaseHandler.insert(
                favoriteTable.TABLE_NAME,
                values!!
            )
            else -> 0
        }

        context?.contentResolver?.notifyChange(favoriteTable.CONTENT_URI, null)
        return Uri.parse("${favoriteTable.CONTENT_URI}/$added")
    }


    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val updated: Int = when (FAVORITE_ID) {
            sUriMatcher.match(uri) -> databaseHandler.update(
                favoriteTable.TABLE_NAME,
                favoriteTable._ID,
                uri.lastPathSegment.toString(),
                values!!
            )
            else -> 0
        }

        context?.contentResolver?.notifyChange(favoriteTable.CONTENT_URI, null)
        return updated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when (FAVORITE_ID) {
            sUriMatcher.match(uri) -> databaseHandler.delete(
                favoriteTable.TABLE_NAME,
                favoriteTable.KEY_USERNAME,
                uri.lastPathSegment.toString()
            )
            else -> 0
        }

        context?.contentResolver?.notifyChange(favoriteTable.CONTENT_URI, null)
        return deleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }


}