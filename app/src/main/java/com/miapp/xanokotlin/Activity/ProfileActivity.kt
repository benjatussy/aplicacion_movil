package com.miapp.xanokotlin.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityProfileBinding
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager
    private val apiService: ApiService by lazy {
        // CORRECCIÓN: Volviendo a usar el Product Service, como probablemente estaba antes.
        ApiService.createProductService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Perfil"

        sessionManager = SessionManager(this)
        loadProfile()

        binding.btnLogout.setOnClickListener {
            sessionManager.clear()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadProfile() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error: Sesión no encontrada. Por favor, inicie sesión de nuevo.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val fullToken = "Bearer $token"
        Log.d("ProfileActivity", "✅ INTENTANDO OBTENER PERFIL CON TOKEN: $fullToken")

        lifecycleScope.launch {
            try {
                val response = apiService.getProfile(fullToken)
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    binding.tvUserName.text = profile.name
                    binding.tvUserEmail.text = profile.email
                    Log.d("ProfileActivity", "✅ PERFIL OBTENIDO CORRECTAMENTE para ${profile.name}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProfileActivity", "❌ ERROR DESDE EL SERVIDOR: Código ${response.code()}")
                    Log.e("ProfileActivity", "❌ CUERPO DEL ERROR: $errorBody")
                    Toast.makeText(this@ProfileActivity, "Error al cargar el perfil (Servidor rechazó el token)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "❌ EXCEPCIÓN DE RED: ${e.message}", e)
                Toast.makeText(this@ProfileActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}