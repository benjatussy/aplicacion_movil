package com.miapp.xanokotlin.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanokotlin.Adaptador.ProductAdapter
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityCartBinding
import com.miapp.xanokotlin.model.CartItem
import com.miapp.xanokotlin.model.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var sessionManager: SessionManager
    private val productService: ApiService by lazy { ApiService.createProductService() }
    private val authService: ApiService by lazy { ApiService.createAuthService() }
    private var finalCartProducts: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        supportActionBar?.title = "Mi Carrito"

        setupRecyclerView()
        loadCartItems()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList())
        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnFinalPay.setOnClickListener {
            if (finalCartProducts.isNotEmpty()) {
                // Note: This total doesn't account for quantity yet. We'll fix this.
                val total = finalCartProducts.sumOf { it.price }
                Toast.makeText(this, "Pago realizado por un total de $${String.format("%.2f", total)}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCartItems() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                // 1. Fetch the list of basic CartItems (shirt_id, quantity)
                val cartItemsResponse = productService.getCartItems("Bearer $token")
                if (cartItemsResponse.isSuccessful && cartItemsResponse.body() != null) {
                    val cartItems = cartItemsResponse.body()!!
                    if (cartItems.isEmpty()) {
                        Toast.makeText(this@CartActivity, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // 2. For each CartItem, fetch its full Product details concurrently
                    val productDetailsJobs = cartItems.map { cartItem ->
                        async { productService.getProduct(cartItem.shirtId) } 
                    }
                    
                    // 3. Wait for all product detail calls to complete
                    val productDetailResponses = productDetailsJobs.awaitAll()

                    // 4. Create the final list of full Product objects
                    finalCartProducts = productDetailResponses.mapNotNull { response ->
                        if (response.isSuccessful) response.body() else null
                    }

                    adapter.updateProducts(finalCartProducts)
                    updateTotal()

                } else {
                    val errorBody = cartItemsResponse.errorBody()?.string()
                    Log.e("CartActivity", "Error al cargar el carrito: ${cartItemsResponse.code()} - $errorBody")
                    Toast.makeText(this@CartActivity, "Error al cargar el carrito", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("CartActivity", "Excepción al cargar el carrito", e)
                Toast.makeText(this@CartActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        if (finalCartProducts.isNotEmpty()) {
            // Note: This total is also temporary as it doesn't use quantity.
            val total = finalCartProducts.sumOf { it.price }
            binding.tvTotalPrice.text = "$${String.format("%.2f", total)}"
        } else {
            binding.tvTotalPrice.text = "$0.00"
        }
    }
}