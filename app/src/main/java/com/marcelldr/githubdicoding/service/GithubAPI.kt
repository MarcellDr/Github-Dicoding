package com.marcelldr.githubdicoding.service

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.marcelldr.githubdicoding.BuildConfig
import com.marcelldr.githubdicoding.model.UserDetailModel
import com.marcelldr.githubdicoding.model.UserSearchModel
import org.json.JSONArray
import org.json.JSONObject

class GithubAPI(context: Context) {
    private val ctx = context

    companion object {
        const val BASE_URL_USERS: String = "https://api.github.com/search/users?q={username}"
        const val BASE_URL_DETAIL: String = "https://api.github.com/users/{username}"
        const val AUTH_KEY: String = BuildConfig.GITHUB_TOKEN
    }

    fun getUsers(query: String? = "MarcellDr"): ArrayList<UserSearchModel> {
        val listUser = ArrayList<UserSearchModel>()
        AndroidNetworking.initialize(ctx)
        val request = AndroidNetworking.get(BASE_URL_USERS)
            .addPathParameter("username", query)
            .addHeaders("Authorization", AUTH_KEY)
            .setPriority(Priority.LOW)
            .build()
        val response = request.executeForJSONObject()
        if (response.isSuccess) {
            val json: JSONObject = response.result as JSONObject
            val users = json.getJSONArray("items")
            for (i in 0 until users.length()) {
                val user = users.getJSONObject(i)
                listUser.add(
                    UserSearchModel(
                        user.getString("login"),
                        user.getString("avatar_url"),
                        user.getString("html_url")
                    )
                )
            }
            Log.i("UserSuccess", "UserSuccess")
        } else {
            Log.i("UserError", response.error.message ?: "UserError")
        }
        Log.i("UserDone", "UserDone")
        return listUser
    }

    fun getDetail(query: String?): UserDetailModel? {
        var userDetail: UserDetailModel? = null
        AndroidNetworking.initialize(ctx)
        val request = AndroidNetworking.get(BASE_URL_DETAIL)
            .addPathParameter("username", query)
            .addHeaders("Authorization", AUTH_KEY)
            .setPriority(Priority.LOW)
            .build()
        val response = request.executeForJSONObject()
        if (response.isSuccess) {
            val json: JSONObject = response.result as JSONObject
            userDetail = UserDetailModel(
                json.getString("login"),
                json.getString("name"),
                json.getString("avatar_url"),
                json.getString("company"),
                json.getString("location"),
                json.getInt("public_repos"),
                json.getInt("followers"),
                json.getInt("following")
            )
            Log.i("DetailSuccess", "DetailSuccess")
        } else {
            Log.i("DetailError", response.error.message ?: "DetailError")
        }
        Log.i("DetailDone", "DetailDone")
        return userDetail
    }

    fun getFollows(url: String?, query: String?): ArrayList<UserSearchModel> {
        val listFollows = ArrayList<UserSearchModel>()
        AndroidNetworking.initialize(ctx)
        val request = AndroidNetworking.get(url)
            .addPathParameter("username", query)
            .addHeaders("Authorization", AUTH_KEY)
            .setPriority(Priority.LOW)
            .build()
        val response = request.executeForJSONArray()
        if (response.isSuccess) {
            val follows = response.result as JSONArray
            for (i in 0 until follows.length()) {
                val user = follows.getJSONObject(i)
                listFollows.add(
                    UserSearchModel(
                        user.getString("login"),
                        user.getString("avatar_url"),
                        user.getString("html_url")
                    )
                )
            }
        } else {
            Log.i("FollowError", response.error.message ?: "FollowError")
        }
        return listFollows
    }
}