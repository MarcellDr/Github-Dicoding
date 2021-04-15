package com.marcelldr.githubdicoding.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.adapter.UserRVAdapter
import com.marcelldr.githubdicoding.custom.CustomLoading
import com.marcelldr.githubdicoding.databinding.ActivityMainBinding
import com.marcelldr.githubdicoding.model.UserSearchModel
import com.marcelldr.githubdicoding.service.GithubAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LIST_USER = "list_user"

class MainActivity : AppCompatActivity() {
    private var listUserSearch: ArrayList<UserSearchModel> = ArrayList()
    private lateinit var binding: ActivityMainBinding
    private lateinit var githubAPI: GithubAPI
    private lateinit var userRVAdapter: UserRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        githubAPI = GithubAPI(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noStatusBar()
        getUIReady()

        lifecycleScope.launch(Dispatchers.Default) {
            listUserSearch = if (savedInstanceState != null) {
                savedInstanceState.getParcelableArrayList<UserSearchModel>(LIST_USER) as ArrayList<UserSearchModel>
            } else {
                val userTask = async(context = Dispatchers.IO) { githubAPI.getUsers() }
                userTask.await()
            }
            withContext(Dispatchers.Main) {
                dismissShimmer()
                showRV()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(LIST_USER, listUserSearch)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        binding.userLoading.shimmerFrameLayout.startShimmerAnimation()
    }

    override fun onPause() {
        binding.userLoading.shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }

    private fun alert(message: String) {
        binding.userLoading.shimmerFrameLayout.stopShimmerAnimation()
        binding.userLoading.shimmerFrameLayout.visibility = View.GONE
        binding.userRV.visibility = View.GONE
        binding.alert.container.visibility = View.VISIBLE
        binding.alert.message.text = message
    }

    private fun showShimmer() {
        binding.userLoading.shimmerFrameLayout.startShimmerAnimation()
        binding.userLoading.shimmerFrameLayout.visibility = View.VISIBLE
        binding.userRV.visibility = View.GONE
        binding.alert.container.visibility = View.GONE
    }

    private fun dismissShimmer() {
        binding.userLoading.shimmerFrameLayout.stopShimmerAnimation()
        binding.userLoading.shimmerFrameLayout.visibility = View.GONE
        binding.userRV.visibility = View.VISIBLE
        binding.alert.container.visibility = View.GONE
    }

    private fun showPopupMenu(v: View) {
        val popupMenu = PopupMenu(this, v)
        popupMenu.menuInflater.inflate(R.menu.menu_setting, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.language -> {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                }
                R.id.alarm -> {
                    val intent = Intent(this@MainActivity, ReminderActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun showRV() {
        binding.userRV.layoutManager = LinearLayoutManager(this)
        userRVAdapter = UserRVAdapter(listUserSearch)
        binding.userRV.adapter = userRVAdapter
        userRVAdapter.setOnItemClickCallback(object : UserRVAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserSearchModel) {
                val customLoading = CustomLoading(this@MainActivity)
                customLoading.show()

                lifecycleScope.launch(Dispatchers.Default) {
                    val userDetailTask =
                        async(context = Dispatchers.IO) { githubAPI.getDetail(data.username) }
                    val userDetail = userDetailTask.await()
                    if (userDetail != null) {
                        customLoading.dismiss()
                        val intent = Intent(this@MainActivity, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.USER_DETAIL, userDetail)
                        startActivity(intent)
                    } else {
                        withContext(Dispatchers.Main) {
                            customLoading.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Terjadi Kesalahan",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        })

    }

    private fun noStatusBar() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window?.statusBarColor = Color.TRANSPARENT
    }

    private fun getUIReady() {
        binding.navMain.favoriteBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
            startActivity(intent)
        }
        binding.navMain.settingBtn.setOnClickListener {
            showPopupMenu(it)
        }
        binding.navMain.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "") {
                    showShimmer()
                    lifecycleScope.launch(Dispatchers.Default) {
                        val userTask =
                            async(context = Dispatchers.IO) { githubAPI.getUsers(s.toString()) }
                        listUserSearch = userTask.await()

                        withContext(context = Dispatchers.Main) {
                            if (listUserSearch.size > 0) {
                                dismissShimmer()
                                showRV()
                            } else {
                                alert("No Data")
                            }

                        }
                    }
                } else {
                    alert("No Entry")
                }
            }
        })
    }
}