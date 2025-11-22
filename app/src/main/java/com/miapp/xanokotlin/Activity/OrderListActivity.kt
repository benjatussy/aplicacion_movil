package com.miapp.xanokotlin.Activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanokotlin.Adaptador.OrderAdapter
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityOrderListBinding
import kotlinx.coroutines.launch

class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var sessionManager: SessionManager
    private val apiService: ApiService by lazy {
        ApiService.createProductService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Gestionar Pedidos"

        sessionManager = SessionManager(this)
        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            emptyList(),
            onAcceptClick = { order ->
                updateOrderStatus(order.id, true)
            },
            onRejectClick = { order ->
                updateOrderStatus(order.id, false)
            }
        )

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderListActivity)
            adapter = orderAdapter
        }
    }

    private fun loadOrders() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Sesión expirada.", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = apiService.getOrders("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    orderAdapter.updateOrders(response.body()!!)
                } else {
                    handleApiError("cargar pedidos", response.code(), response.errorBody()?.string())
                }
            } catch (e: Exception) {
                handleConnectionError("cargar pedidos", e)
            }
        }
    }

    private fun updateOrderStatus(orderId: Int, accepted: Boolean) {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Sesión expirada.", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = if (accepted) {
                    apiService.acceptOrder("Bearer $token", orderId)
                } else {
                    apiService.rejectOrder("Bearer $token", orderId)
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@OrderListActivity, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                    loadOrders() // Recargar la lista para ver el cambio de estado
                } else {
                    handleApiError("actualizar pedido", response.code(), response.errorBody()?.string())
                }
            } catch (e: Exception) {
                handleConnectionError("actualizar pedido", e)
            }
        }
    }

    private fun handleApiError(action: String, code: Int, errorBody: String?) {
        Log.e("OrderListActivity", "Error al $action: $code - $errorBody")
        Toast.makeText(this, "Error al $action", Toast.LENGTH_SHORT).show()
    }

    private fun handleConnectionError(action: String, e: Exception) {
        Log.e("OrderListActivity", "Excepción al $action", e)
        Toast.makeText(this, "Error de conexión al $action", Toast.LENGTH_SHORT).show()
    }
}