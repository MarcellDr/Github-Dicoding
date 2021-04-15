package com.marcelldr.consumerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSearchModel(
    val username: String? = "",
    val avatar: String? = "",
    val link: String? = ""
) : Parcelable