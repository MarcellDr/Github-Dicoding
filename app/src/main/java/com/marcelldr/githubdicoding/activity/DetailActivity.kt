package com.marcelldr.githubdicoding.activity

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.adapter.FollowTabAdapter
import com.marcelldr.githubdicoding.database.DatabaseHandler
import com.marcelldr.githubdicoding.database.DatabaseSchema
import com.marcelldr.githubdicoding.databinding.ActivityDetailBinding
import com.marcelldr.githubdicoding.model.UserDetailModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var userDetail: UserDetailModel
    private lateinit var tabTitle: ArrayList<String>
    private lateinit var databaseHandler: DatabaseHandler
    private var favorite: Boolean = false

    companion object {
        const val USER_DETAIL = "user_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        userDetail = intent.getParcelableExtra<UserDetailModel>(USER_DETAIL) as UserDetailModel
        databaseHandler = DatabaseHandler.getInstance(applicationContext)
        setContentView(binding.root)
        noStatusBar()
        getUIReady(userDetail)
    }

    private fun noStatusBar() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.statusBarColor = Color.TRANSPARENT
    }

    private fun getUIReady(userDetail: UserDetailModel) {
        binding.detailUser.detailUsername.text = userDetail.username
        binding.detailUser.detailName.text = userDetail.name
        binding.detailUser.detailRepo.text = StringBuilder("Repositories: ").append(userDetail.repo.toString())
        binding.detailUser.detailDescription.text = StringBuilder(userDetail.company ?: "").append(" - ").append(userDetail.location ?: "")
        Glide.with(binding.detailUser.detailAvatar.context).load(userDetail.avatar)
            .into(binding.detailUser.detailAvatar)

        databaseHandler.open()
        val result: Cursor = databaseHandler.where(DatabaseSchema.FavoriteTable.TABLE_NAME,
                DatabaseSchema.FavoriteTable.KEY_USERNAME,
                userDetail.username)
        if(result.count > 0) {
            favorite = !favorite
        }
        databaseHandler.close()
        binding.detailUser.toggleFavorite.isChecked = favorite

        binding.detailUser.backButton.setOnClickListener { finish() }
        binding.detailUser.toggleFavorite.setOnClickListener {
            databaseHandler.open()
            if(favorite) {
                favorite = !favorite
                binding.detailUser.toggleFavorite.isChecked = favorite
                databaseHandler.delete(DatabaseSchema.FavoriteTable.TABLE_NAME,
                        DatabaseSchema.FavoriteTable.KEY_USERNAME,
                        userDetail.username)
                Toast.makeText(applicationContext, "${userDetail.username} dihapus dari Favorite", Toast.LENGTH_SHORT).show()
            } else {
                favorite = !favorite
                binding.detailUser.toggleFavorite.isChecked = favorite
                val values = ContentValues()
                values.put(DatabaseSchema.FavoriteTable.KEY_USERNAME, userDetail.username)
                values.put(DatabaseSchema.FavoriteTable.KEY_NAME, userDetail.name)
                values.put(DatabaseSchema.FavoriteTable.KEY_AVATAR, userDetail.avatar)
                values.put(DatabaseSchema.FavoriteTable.KEY_COMPANY, userDetail.company)
                values.put(DatabaseSchema.FavoriteTable.KEY_LOCATION, userDetail.location)
                values.put(DatabaseSchema.FavoriteTable.KEY_REPO, userDetail.repo)
                values.put(DatabaseSchema.FavoriteTable.KEY_FOLLOWER, userDetail.follower)
                values.put(DatabaseSchema.FavoriteTable.KEY_FOLLOWING, userDetail.following)
                databaseHandler.insert(DatabaseSchema.FavoriteTable.TABLE_NAME, values)
                Toast.makeText(applicationContext, "${userDetail.username} masuk ke Favorite", Toast.LENGTH_SHORT).show()
            }
            databaseHandler.close()
        }

        tabTitle = ArrayList()
        tabTitle.add("${resources.getString(R.string.followers)} (${userDetail.follower.toString()})")
        tabTitle.add("${resources.getString(R.string.following)} (${userDetail.following.toString()})")
        val followTabAdapter = FollowTabAdapter(this)
        followTabAdapter.username = userDetail.username
        binding.followPager.adapter = followTabAdapter
        TabLayoutMediator(
            binding.followTabManager,
            binding.followPager
        ) { tab, position -> tab.text = tabTitle[position] }.attach()

    }

    override fun onResume() {
        databaseHandler.open()
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        databaseHandler.close()
    }
    override fun onDestroy() {
        super.onDestroy()
        databaseHandler.close()
    }
}