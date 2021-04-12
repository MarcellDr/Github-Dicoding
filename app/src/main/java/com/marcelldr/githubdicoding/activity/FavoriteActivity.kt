package com.marcelldr.githubdicoding.activity

import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.marcelldr.githubdicoding.adapter.FavoriteRVAdapter
import com.marcelldr.githubdicoding.database.DatabaseHandler
import com.marcelldr.githubdicoding.database.DatabaseSchema
import com.marcelldr.githubdicoding.databinding.ActivityFavoriteBinding
import com.marcelldr.githubdicoding.model.UserDetailModel

class FavoriteActivity : AppCompatActivity() {
    private var listUserFavorite: ArrayList<UserDetailModel> = ArrayList()
    private var filter: ArrayList<UserDetailModel> = ArrayList()
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
        binding.navFavorite.backButton.setOnClickListener { finish() }
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
        if (listUserFavorite.size == 0) {
            binding.alert.container.visibility = View.VISIBLE
        } else {
            filter = listUserFavorite
        }
    }

    private fun showRV() {
        binding.favoriteRV.layoutManager = LinearLayoutManager(this)
        favoriteRVAdapter = FavoriteRVAdapter(filter)
        binding.favoriteRV.adapter = favoriteRVAdapter

        favoriteRVAdapter.setOnItemClickCallback(object : FavoriteRVAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserDetailModel) {
                databaseHandler.open()
                databaseHandler.delete(
                    DatabaseSchema.FavoriteTable.TABLE_NAME,
                    DatabaseSchema.FavoriteTable.KEY_USERNAME,
                    data.username
                )
                databaseHandler.close()
                listUserFavorite.removeIf { userFavorite: UserDetailModel -> userFavorite.username == data.username }
                filter.removeIf { userFavorite: UserDetailModel -> userFavorite.username == data.username }
                if (listUserFavorite.size == 0) {
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