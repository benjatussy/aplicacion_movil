package com.miapp.xanokotlin.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.databinding.ActivityUserEditBinding
import com.miapp.xanokotlin.model.User
import com.miapp.xanokotlin.model.UserRequest
import com.miapp.xanokotlin.SessionManager
import kotlinx.coroutines.launch
import java.io.Serializable

class UserEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserEditBinding
    private val apiService: ApiService by lazy { ApiService.createProductService() }
    private lateinit var sessionManager: SessionManager
    private var userToEdit: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        userToEdit = intent.getSerializableExtra("USER_TO_EDIT") as? User

        if (userToEdit == null) {
            Toast.makeText(this, "Error: No se pudieron cargar los datos del usuario", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val currentUser = userToEdit!!
        binding.etNombre.setText(currentUser.name)
        binding.etEmail.setText(currentUser.email)
        binding.etRol.setText(currentUser.rol)
        binding.etEstado.setText(currentUser.status)
        supportActionBar?.title = "Editar: ${currentUser.name}"

        binding.btnActualizar.setOnClickListener {
            actualizarUsuario(currentUser.id)
        }

        binding.btnEliminar.setOnClickListener {
            eliminarUsuario(currentUser.id)
        }
    }

    private fun actualizarUsuario(userId: Int) {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        val name = binding.etNombre.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val role = binding.etRol.text.toString().trim()
        val status = binding.etEstado.text.toString().trim()

        if (name.isBlank() || email.isBlank() || role.isBlank() || status.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UserRequest(name = name, email = email, role = role, status = status)

        lifecycleScope.launch {
            try {
                val response = apiService.updateUser("Bearer $token", userId, request)
                if (response.isSuccessful) {
                    Toast.makeText(this@UserEditActivity, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@UserEditActivity, "Error al actualizar: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UserEditActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun eliminarUsuario(userId: Int) {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Error de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = apiService.deleteUser("Bearer $token", userId)
                if (response.isSuccessful) {
                    Toast.makeText(this@UserEditActivity, "Usuario eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@UserEditActivity, "Error al eliminar: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UserEditActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}