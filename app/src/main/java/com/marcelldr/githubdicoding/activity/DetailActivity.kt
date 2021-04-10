package com.marcelldr.githubdicoding.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.adapter.FollowTabAdapter
import com.marcelldr.githubdicoding.databinding.ActivityDetailBinding
import com.marcelldr.githubdicoding.model.UserDetailModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var userDetail: UserDetailModel
    private lateinit var tabTitle: ArrayList<String>

    companion object {
        const val USER_DETAIL = "user_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        userDetail = intent.getParcelableExtra<UserDetailModel>(USER_DETAIL) as UserDetailModel
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
        if(userDetail.favorite) {
            Glide.with(binding.detailUser.favorite.context).load(R.drawable.heart)
                .into(binding.detailUser.favorite)
        }

        binding.detailUser.arrowBack.setOnClickListener { finish() }
        binding.detailUser.favorite.setOnClickListener {
            if(userDetail.favorite) {
                userDetail.favorite = !userDetail.favorite
                Glide.with(binding.detailUser.favorite.context).load(R.drawable.heart_white)
                    .into(binding.detailUser.favorite)
                Toast.makeText(applicationContext, "${userDetail.username} dihapus dari Favorite", Toast.LENGTH_SHORT).show()
            } else {
                userDetail.favorite = !userDetail.favorite
                Glide.with(binding.detailUser.favorite.context).load(R.drawable.heart)
                    .into(binding.detailUser.favorite)
                Toast.makeText(applicationContext, "${userDetail.username} masuk ke Favorite", Toast.LENGTH_SHORT).show()

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