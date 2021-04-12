package com.marcelldr.githubdicoding.activity

import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.adapter.FavoriteRVAdapter
import com.marcelldr.githubdicoding.database.DatabaseHandler
import com.marcelldr.githubdicoding.database.DatabaseSchema
import com.marcelldr.githubdicoding.databinding.ActivityFavoriteBinding
import com.marcelldr.githubdicoding.model.UserDetailModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteActivity : AppCompatActivity() {
    private var listUserFavorite: ArrayList<UserDetailModel> = ArrayList()
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteRVAdapter: FavoriteRVAdapter
    private lateinit var databaseHandler: DatabaseHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        databaseHandler = DatabaseHandler.getInstance(applicationContext)
        setContentView(binding.root)

        noStatusBar()
        getUIReady()
        getFavorite()
        showRV()

    }

    private fun noStatusBar() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.statusBarColor = Color.TRANSPARENT
    }

    private fun getUIReady() {
        binding.navbar.backButton.setOnClickListener { finish() }
    }

    private fun getFavorite() {
        databaseHandler.open()
        val favoriteCursor: Cursor = databaseHandler.getAll(DatabaseSchema.FavoriteTable.TABLE_NAME)
        favoriteCursor.apply {
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
        databaseHandler.close()
        if(listUserFavorite.size == 0) {
            binding.alert.container.visibility = View.VISIBLE
        }
    }

    private fun showRV() {
        binding.favoriteRV.layoutManager = LinearLayoutManager(this)
        favoriteRVAdapter = FavoriteRVAdapter(listUserFavorite)
        binding.favoriteRV.adapter = favoriteRVAdapter

        favoriteRVAdapter.setOnItemClickCallback(object: FavoriteRVAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserDetailModel) {
                databaseHandler.open()
                databaseHandler.delete(DatabaseSchema.FavoriteTable.TABLE_NAME,
                        DatabaseSchema.FavoriteTable.KEY_USERNAME,
                        data.username)
                databaseHandler.close()
                listUserFavorite.removeIf{userFavorite: UserDetailModel -> userFavorite.username == data.username}
                if(listUserFavorite.size == 0) {
                    binding.alert.container.visibility = View.VISIBLE
                }
                Toast.makeText(applicationContext, "${data.username} dihapus dari Favorite", Toast.LENGTH_SHORT).show()
            }
        })
    }
}