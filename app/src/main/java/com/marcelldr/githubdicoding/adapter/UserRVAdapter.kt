package com.marcelldr.githubdicoding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.marcelldr.githubdicoding.R
import com.marcelldr.githubdicoding.model.UserModel

class UserRVAdapter(private val listUser: ArrayList<UserModel>) :
    RecyclerView.Adapter<UserRVAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userUsername: TextView = itemView.findViewById(R.id.userUsername)
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        val userLink: TextView = itemView.findViewById(R.id.userLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        holder.userUsername.text = user.username
        holder.userLink.text = user.link
        Glide.with(holder.userAvatar.context).load(user.avatar).into(holder.userAvatar)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUser[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UserModel)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}