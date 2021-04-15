package com.marcelldr.consumerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marcelldr.consumerapp.R
import com.marcelldr.consumerapp.databinding.UserListItemBinding
import com.marcelldr.consumerapp.model.UserSearchModel

class UserRVAdapter(private val listUserSearch: ArrayList<UserSearchModel>) :
    RecyclerView.Adapter<UserRVAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = UserListItemBinding.bind(itemView)
        fun bind(userSearch: UserSearchModel) {
            binding.userUsername.text = userSearch.username
            binding.userLink.text = userSearch.link
            Glide.with(binding.userAvatar.context).load(userSearch.avatar).into(binding.userAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listUserSearch[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUserSearch[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return listUserSearch.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserSearchModel)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}