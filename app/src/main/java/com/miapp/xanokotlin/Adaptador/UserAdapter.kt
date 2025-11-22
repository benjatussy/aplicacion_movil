package com.miapp.xanokotlin.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanokotlin.databinding.ItemUserBinding
import com.miapp.xanokotlin.model.User

class UserAdapter(
    private var users: List<User>,
    private val onEditClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserRole.text = "Rol: ${user.rol}"
            binding.tvUserStatus.text = "Estado: ${user.status}"

            binding.btnEditUser.setOnClickListener { onEditClick(user) }
            // Se elimina la lógica de bloqueo de este adaptador
            binding.btnToggleBlock.visibility = View.GONE 
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}