package com.marcelldr.consumerapp.activity

import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.marcelldr.consumerapp.adapter.FavoriteRVAdapter
import com.marcelldr.consumerapp.database.DatabaseSchema
import com.marcelldr.consumerapp.databinding.ActivityFavoriteBinding
import com.marcelldr.consumerapp.model.UserDetailModel

class FavoriteActivity : AppCompatActivity() {
    private var listUserFavorite: ArrayList<UserDetailModel> = ArrayList()
    private var filter: ArrayList<UserDetailModel> = ArrayList()
    private var favoriteTable = DatabaseSchema.FavoriteTable
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteRVAdapter: FavoriteRVAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noStatusBar()
        watchContentProvider()
        getFavorite()
        getUIReady()
        showRV()

    }

    private fun noStatusBar() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.statusBarColor = Color.TRANSPARENT
    }

    private fun watchContentProvider() {
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                getFavorite()
                runOnUiThread {
                    if (listUserFavorite.size == 0) {
                        binding.alert.container.visibility = View.VISIBLE
                    }
                    showRV()
                }
            }
        }

        contentResolver.registerContentObserver(favoriteTable.CONTENT_URI, true, myObserver)
    }

    private fun getUIReady() {
        filter = listUserFavorite
        if (filter.size == 0) {
            binding.alert.container.visibility = View.VISIBLE
        }
        binding.navFavorite.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == "") {
                    filter = listUserFavorite
                    binding.alert.container.visibility = View.VISIBLE
                } else {
                    filter = listUserFavorite.filter { it ->
                        it.username?.contains(s!!, ignoreCase = true) == true
                    } as ArrayList<UserDetailModel>
                }
                if (filter.size > 0) {
                    binding.alert.container.visibility = View.GONE
                } else {
                    binding.alert.container.visibility = View.VISIBLE
                }
                showRV()
            }
        })
    }

    private fun getFavorite() {
        listUserFavorite = ArrayList()
        val favoriteCursor: Cursor? =
            contentResolver.query(favoriteTable.CONTENT_URI, null, null, null, null)
        favoriteCursor!!.apply {
            while (moveToNext()) {
                val userFavorite = UserDetailModel(
                    getString(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_USERNAME)),
                    getString(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_NAME)),
                    getString(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_AVATAR)),
                    getString(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_COMPANY)),
                    getString(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_LOCATION)),
                    getInt(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_REPO)),
                    getInt(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_FOLLOWER)),
                    getInt(getColumnIndexOrThrow(DatabaseSchema.FavoriteTable.KEY_FOLLOWING)),
                )
                listUserFavorite.add(userFavorite)
            }
        }
        favoriteCursor.close()
    }

    private fun showRV() {
        binding.favoriteRV.layoutManager = LinearLayoutManager(this)
        favoriteRVAdapter = FavoriteRVAdapter(filter)
        binding.favoriteRV.adapter = favoriteRVAdapter

        favoriteRVAdapter.setOnItemClickCallback(object : FavoriteRVAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserDetailModel) {
                val userFavoriteUri =
                    Uri.parse(favoriteTable.CONTENT_URI.toString() + "/" + data.username)
                contentResolver.delete(userFavoriteUri, null, null)
                filter.removeIf { userFavorite: UserDetailModel -> userFavorite.username == data.username }
                if (filter.size == 0) {
                    binding.alert.container.visibility = View.VISIBLE
                }
                Toast.makeText(
                    applicationContext,
                    "${data.username} dihapus dari Favorite",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}