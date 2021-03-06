package com.marcelldr.githubdicoding.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSearchModel(
    val username: String? = "",
    val avatar: String? = "",
    val link: String? = ""
) : Parcelable