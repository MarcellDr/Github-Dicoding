package com.marcelldr.consumerapp.activity

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.marcelldr.consumerapp.R
import com.marcelldr.consumerapp.adapter.FollowTabAdapter
import com.marcelldr.consumerapp.database.DatabaseSchema
import com.marcelldr.consumerapp.databinding.ActivityDetailBinding
import com.marcelldr.consumerapp.model.UserDetailModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var userDetail: UserDetailModel
    private lateinit var tabTitle: ArrayList<String>
    private lateinit var userFavoriteUri: Uri
    private var favorite: Boolean = false

    companion object {
        const val USER_DETAIL = "user_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        userDetail = intent.getParcelableExtra<UserDetailModel>(USER_DETAIL) as UserDetailModel
        userFavoriteUri =
            Uri.parse(DatabaseSchema.FavoriteTable.CONTENT_URI.toString() + "/" + userDetail.username)
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
        binding.detailUser.detailRepo.text =
            StringBuilder("Repositories: ").append(userDetail.repo.toString())
        binding.detailUser.detailDescription.text =
            StringBuilder(userDetail.company ?: "").append(" - ").append(userDetail.location ?: "")
        Glide.with(binding.detailUser.detailAvatar.context).load(userDetail.avatar)
            .into(binding.detailUser.detailAvatar)


        val result: Cursor? =
            contentResolver.query(
                userFavoriteUri,
                null,
                null,
                null,
                null
            )
        if (result != null && result.count > 0) {
            favorite = !favorite
        }
        result?.close()
        binding.detailUser.toggleFavorite.isChecked = favorite

        binding.detailUser.backButton.setOnClickListener { finish() }
        binding.detailUser.toggleFavorite.setOnClickListener {
            if (favorite) {
                favorite = !favorite
                binding.detailUser.toggleFavorite.isChecked = favorite
                contentResolver.delete(userFavoriteUri, null, null)
                Toast.makeText(
                    applicationContext,
                    "${userDetail.username} dihapus dari Favorite",
                    Toast.LENGTH_SHORT
                ).show()
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
                contentResolver.insert(DatabaseSchema.FavoriteTable.CONTENT_URI, values)
                Toast.makeText(
                    applicationContext,
                    "${userDetail.username} masuk ke Favorite",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
}