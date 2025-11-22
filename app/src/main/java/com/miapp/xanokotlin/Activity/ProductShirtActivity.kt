package com.miapp.xanokotlin.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanokotlin.Adaptador.ProductAdapter
import com.miapp.xanokotlin.R
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityProductShirtBinding
import kotlinx.coroutines.launch

class ProductShirtActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductShirtBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ProductAdapter

    private val authService: ApiService by lazy {
        ApiService.createAuthService()
    }
    private val productService: ApiService by lazy {
        ApiService.createProductService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductShirtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setupRecyclerView()
        setupListeners()
        setupRoleBasedVisibility()

        loadUserProfile()
        loadProducts()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupRoleBasedVisibility() {
        val userRole = sessionManager.getRole()
        val addProductItem = binding.bottomNavigation.menu.findItem(R.id.nav_add_product)
        val cartItem = binding.bottomNavigation.menu.findItem(R.id.nav_cart)

        if (userRole.equals("admin", ignoreCase = true)) {
            addProductItem.isVisible = true
            cartItem.isVisible = false
        } else {
            addProductItem.isVisible = false
            cartItem.isVisible = true
        }
    }

    private fun setupListeners() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_product -> {
                    startActivity(Intent(this, AddProductActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserProfile() {
        val token = sessionManager.getToken()
        if (token == null) {
            supportActionBar?.title = "Bienvenido"
            return
        }

        lifecycleScope.launch {
            try {
                val response = authService.getProfile("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    supportActionBar?.title = "Bienvenido, ${user.name} ⚽"
                } else {
                    supportActionBar?.title = "Bienvenido"
                }
            } catch (e: Exception) {
                supportActionBar?.title = "Bienvenido"
                Log.e("ProductShirtActivity", "Excepción al cargar perfil de usuario", e)
            }
        }
    }

    private fun loadProducts() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = productService.getProducts("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    adapter.updateProducts(products)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductShirtActivity", "Error al cargar productos: ${response.code()} - $errorBody")
                    Toast.makeText(this@ProductShirtActivity, "Error al cargar los productos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProductShirtActivity", "Excepción al cargar productos", e)
                Toast.makeText(this@ProductShirtActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}