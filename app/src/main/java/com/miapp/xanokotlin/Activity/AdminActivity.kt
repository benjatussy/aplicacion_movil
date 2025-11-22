package com.miapp.xanokotlin.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.miapp.xanokotlin.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Panel de Administrador"

        binding.btnManageProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.btnManageUsers.setOnClickListener {
            startActivity(Intent(this, UserListActivity::class.java))
        }

        binding.btnManageOrders.setOnClickListener {
            startActivity(Intent(this, OrderListActivity::class.java))
        }

        binding.btnManageProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}