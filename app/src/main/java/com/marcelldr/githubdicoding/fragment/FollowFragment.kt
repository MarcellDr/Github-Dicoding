package com.marcelldr.githubdicoding.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.activity.DetailActivity
import com.marcelldr.githubdicoding.adapter.UserRVAdapter
import com.marcelldr.githubdicoding.custom.CustomLoading
import com.marcelldr.githubdicoding.databinding.FragmentFollowBinding
import com.marcelldr.githubdicoding.model.UserModel
import com.marcelldr.githubdicoding.service.GithubAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val USERNAME = "username"
private const val URL = "url"

class FollowFragment : Fragment() {
    private var username: String? = null
    private var url: String? = null
    private var listFollow: ArrayList<UserModel> = ArrayList()
    private lateinit var githubAPI: GithubAPI
    private lateinit var followRVAdapter: UserRVAdapter
    private lateinit var binding: FragmentFollowBinding


    companion object {
        @JvmStatic
        fun newInstance(username: String?, url: String?) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putString(USERNAME, username)
                    putString(URL, url)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(USERNAME)
            url = it.getString(URL)
        }
    }

    @SuppressLint("ShowToast")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        githubAPI = GithubAPI(requireContext())
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        lifecycleScope.launch(Dispatchers.Default) {
            val getFollowsTask =
                async(context = Dispatchers.IO) { githubAPI.getFollows(url, username) }
            listFollow = getFollowsTask.await()
            withContext(Dispatchers.Main) {
                if (listFollow.size == 0) {
                    dismissShimer()
                    Toast.makeText(
                        requireContext(),
                        "There is No Followers / Following!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    dismissShimer()
                    showRV()
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.followLoading.shimmerFrameLayout.startShimmerAnimation()
    }

    override fun onPause() {
        binding.followLoading.shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }

    private fun dismissShimer() {
        binding.followLoading.shimmerFrameLayout.stopShimmerAnimation()
        binding.followLoading.shimmerFrameLayout.visibility = View.GONE
        binding.followRV.visibility = View.VISIBLE
    }

    private fun showRV() {
        binding.followRV.layoutManager = LinearLayoutManager(requireContext())
        followRVAdapter = UserRVAdapter(listFollow)
        binding.followRV.adapter = followRVAdapter
        followRVAdapter.setOnItemClickCallback(object : UserRVAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserModel) {
                val customLoading = CustomLoading(activity as AppCompatActivity)
                customLoading.show()

                lifecycleScope.launch(Dispatchers.Default) {
                    val userDetailTask =
                        async(context = Dispatchers.IO) { githubAPI.getDetail(data.username) }
                    val userDetail = userDetailTask.await()
                    if (userDetail != null) {
                        customLoading.dismiss()
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra(DetailActivity.USER_DETAIL, userDetail)
                        startActivity(intent)
//                        (activity as AppCompatActivity).finish()
                    } else {
                        withContext(Dispatchers.Main) {
                            customLoading.dismiss()
                            Toast.makeText(
                                requireContext(),
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
}