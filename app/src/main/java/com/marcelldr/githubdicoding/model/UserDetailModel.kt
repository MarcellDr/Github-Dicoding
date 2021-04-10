package com.marcelldr.githubdicoding.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailModel(
    val username: String? = "",
    val name: String? = "",
    val avatar: String? = "",
    val company: String? = "",
    val location: String? = "",
    val repo: Int? = 0,
    val follower: Int? = 0,
    val following: Int? = 0
) : Parcelable