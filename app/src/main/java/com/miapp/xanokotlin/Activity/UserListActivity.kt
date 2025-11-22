package com.miapp.xanokotlin.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanokotlin.Adaptador.UserAdapter
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityUserListBinding
import com.miapp.xanokotlin.model.User
import kotlinx.coroutines.launch

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserListBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var sessionManager: SessionManager

    private val apiService by lazy { ApiService.createProductService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Gestionar Usuarios"

        sessionManager = SessionManager(this)
        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            emptyList(),
            onEditClick = { user ->
                val intent = Intent(this, UserEditActivity::class.java).apply {
                    putExtra("USER_TO_EDIT", user)
                }
                startActivity(intent)
            }
        )

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UserListActivity)
            adapter = userAdapter
        }
    }

    private fun loadUsers() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Sesión expirada. Por favor, inicie sesión de nuevo.", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = apiService.getUsers("Bearer $token")
                if (response.isSuccessful) {
                    val userList = response.body() ?: emptyList()
                    userAdapter.updateUsers(userList)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UserListActivity", "Error al cargar usuarios: ${response.code()} - $errorBody")
                    Toast.makeText(this@UserListActivity, "Error al cargar la lista de usuarios", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("UserListActivity", "Excepción al cargar usuarios", e)
                Toast.makeText(this@UserListActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}