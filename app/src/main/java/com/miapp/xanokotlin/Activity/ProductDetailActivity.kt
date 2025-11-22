package com.miapp.xanokotlin.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.miapp.xanokotlin.Adaptador.ImageSliderAdapter
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityProductDetailBinding
import com.miapp.xanokotlin.model.AddToCartRequest
import com.miapp.xanokotlin.model.Product
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var sessionManager: SessionManager
    private val productService: ApiService by lazy { ApiService.createProductService() }
    private var productId: Int = -1
    private var currentProduct: Product? = null
    private var quantity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        productId = intent.getIntExtra("PRODUCT_ID", -1)

        if (productId != -1) {
            loadProductDetails()
        } else {
            Toast.makeText(this, "Error: ID de producto no encontrado", Toast.LENGTH_LONG).show()
            finish()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Admin Listeners
        binding.btnDeleteProduct.setOnClickListener { deleteProduct() }
        binding.btnEditProduct.setOnClickListener { Toast.makeText(this, "Función de editar no implementada", Toast.LENGTH_SHORT).show() }

        // Client Listeners
        binding.btnIncrement.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }

        binding.btnDecrement.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun addToCart() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        currentProduct?.let {
            val request = AddToCartRequest(productId = it.id, quantity = quantity)
            lifecycleScope.launch {
                try {
                    val response = productService.addToCart("Bearer $token", request)
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProductDetailActivity, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProductDetail", "Error al añadir al carrito: ${response.code()} - $errorBody")
                        Toast.makeText(this@ProductDetailActivity, "Error al añadir al carrito", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ProductDetail", "Excepción al añadir al carrito", e)
                    Toast.makeText(this@ProductDetailActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this, "Error: no se pudo encontrar el producto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRoleBasedActions() {
        val userRole = sessionManager.getRole()
        if (userRole.equals("admin", ignoreCase = true)) {
            binding.adminActionsContainer.visibility = View.VISIBLE
            binding.clientActionsContainer.visibility = View.GONE
        } else {
            binding.adminActionsContainer.visibility = View.GONE
            binding.clientActionsContainer.visibility = View.VISIBLE
        }
    }

    private fun loadProductDetails() {
        lifecycleScope.launch {
            try {
                val response = productService.getProduct(productId)
                if (response.isSuccessful && response.body() != null) {
                    currentProduct = response.body()!!
                    currentProduct?.let { product ->
                        binding.tvProductNameDetail.text = product.name
                        binding.tvProductDescriptionDetail.text = product.description
                        binding.tvProductPriceDetail.text = "$${product.price}"
                        supportActionBar?.title = product.name

                        product.imageUrls?.let {
                            val imageAdapter = ImageSliderAdapter(it)
                            binding.viewPagerImages.adapter = imageAdapter

                            TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerImages) { _, _ -> }.attach()
                        }

                        setupRoleBasedActions()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductDetail", "Error al cargar producto: ${response.code()} - $errorBody")
                    Toast.makeText(this@ProductDetailActivity, "Error al cargar el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProductDetail", "Excepción al cargar producto", e)
                Toast.makeText(this@ProductDetailActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteProduct() {
        lifecycleScope.launch {
            try {
                val response = productService.deleteProduct(productId)
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductDetailActivity, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductDetail", "Error al eliminar: ${response.code()} - $errorBody")
                    Toast.makeText(this@ProductDetailActivity, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProductDetail", "Excepción al eliminar", e)
                Toast.makeText(this@ProductDetailActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}