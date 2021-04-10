package com.marcelldr.githubdicoding.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.marcelldr.githubdicoding.fragment.FollowFragment

class FollowTabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var username: String? = "username"

    companion object {
        const val BASE_URL_FOLLOWER: String = "https://api.github.com/users/{username}/followers"
        const val BASE_URL_FOLLOWING: String = "https://api.github.com/users/{username}/following"
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FollowFragment.newInstance(username, BASE_URL_FOLLOWER)
            1 -> fragment = FollowFragment.newInstance(username, BASE_URL_FOLLOWING)
        }
        return fragment as Fragment
    }
}