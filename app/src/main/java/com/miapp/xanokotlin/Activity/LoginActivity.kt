package com.miapp.xanokotlin.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanokotlin.SessionManager
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityLoginBinding
import com.miapp.xanokotlin.model.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val authApiService: ApiService by lazy {
        ApiService.createAuthService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "FutPlayers"

        sessionManager = SessionManager(this)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            // TODO: Create RegisterActivity if it doesn't exist
            // startActivity(Intent(this, RegisterActivity::class.java))
            Toast.makeText(this, "Ir a la pantalla de registro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = authApiService.loginUsuario(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    sessionManager.saveToken(loginResponse.authToken)
                    sessionManager.saveUserId(loginResponse.user.id)

                    val userRole = loginResponse.user.rol
                    if (userRole != null) {
                        sessionManager.saveRole(userRole)

                        when (userRole.lowercase()) {
                            "admin" -> {
                                val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            "cliente" -> {
                                val intent = Intent(this@LoginActivity, ProductListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Log.e("LoginActivity", "Rol desconocido: $userRole")
                                Toast.makeText(this@LoginActivity, "Rol de usuario no reconocido", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Log.e("LoginActivity", "El rol del usuario es nulo.")
                        Toast.makeText(this@LoginActivity, "Error: El usuario no tiene un rol asignado.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginActivity", "Error en el login: ${response.code()} - $errorBody")
                    Toast.makeText(this@LoginActivity, "Error: Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Excepción en el login", e)
                Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}