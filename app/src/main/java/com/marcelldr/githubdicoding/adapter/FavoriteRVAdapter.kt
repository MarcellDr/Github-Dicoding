package com.marcelldr.githubdicoding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.databinding.FavoriteListItemBinding
import com.marcelldr.githubdicoding.model.UserDetailModel

class FavoriteRVAdapter(private val listUserFavorite: ArrayList<UserDetailModel>) :
    RecyclerView.Adapter<FavoriteRVAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = FavoriteListItemBinding.bind(itemView)
        fun bind(userFavorite: UserDetailModel) {
            binding.userUsername.text = userFavorite.username
            binding.userFollower.text = StringBuilder("Followers: ").append(userFavorite.follower)
            binding.userFollowing.text = StringBuilder("Following: ").append(userFavorite.following)
            Glide.with(binding.userAvatar.context).load(userFavorite.avatar)
                .into(binding.userAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.favorite_list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUserFavorite[position])
        holder.binding.favorite.setOnClickListener {
            onItemClickCallback.onItemClicked(listUserFavorite[holder.adapterPosition])
            unFavorite(position)
        }
    }

    override fun getItemCount(): Int {
        return listUserFavorite.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserDetailModel)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    private fun unFavorite(position: Int) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listUserFavorite.size)
    }
}