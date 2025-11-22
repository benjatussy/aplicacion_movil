package com.miapp.xanokotlin.Activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.miapp.xanokotlin.R
import com.miapp.xanokotlin.api.ApiService
import com.miapp.xanokotlin.model.CreateProductRequest
import com.miapp.xanokotlin.model.UploadImageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AddProductActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etTeam: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnSaveProduct: Button
    private lateinit var progressBar: ProgressBar

    private val selectedImageUris = mutableListOf<Uri>()

    private val apiService: ApiService by lazy {
        ApiService.createProductService()
    }

    // Selector de múltiples imágenes
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            Toast.makeText(this, "${selectedImageUris.size} imagen(es) seleccionada(s)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.etName)
        etTeam = findViewById(R.id.etTeam)
        etDescription = findViewById(R.id.etDescription)
        etPrice = findViewById(R.id.etPrice)
        etStock = findViewById(R.id.etStock)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSaveProduct = findViewById(R.id.btnSaveProduct)

        // Si tienes un ProgressBar en tu layout, inicialízalo aquí
        // progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnSelectImage.setOnClickListener {
            pickImages.launch("image/*")
        }

        btnSaveProduct.setOnClickListener {
            if (validateInputs()) {
                createProductWithImages()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = etName.text.toString().trim()
        val team = etTeam.text.toString().trim()
        val priceText = etPrice.text.toString().trim()
        val stockText = etStock.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "El nombre del producto es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        if (team.isEmpty()) {
            Toast.makeText(this, "El equipo es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        if (priceText.isEmpty()) {
            Toast.makeText(this, "El precio es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        val price = priceText.toIntOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "El precio debe ser un número válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (stockText.isEmpty()) {
            Toast.makeText(this, "El stock es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        val stock = stockText.toIntOrNull()
        if (stock == null || stock < 0) {
            Toast.makeText(this, "El stock no puede ser negativo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona al menos una imagen", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createProductWithImages() {
        setFormEnabled(false)
        // Si tienes progressBar, descoméntalo: progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // PASO 1: Subir imágenes en paralelo
                val uploadedImages = if (selectedImageUris.isNotEmpty()) {
                    Log.d("AddProductActivity", "Subiendo ${selectedImageUris.size} imágenes...")
                    val uploadTasks = selectedImageUris.map { uri ->
                        async(Dispatchers.IO) {
                            uploadImage(uri)
                        }
                    }
                    uploadTasks.awaitAll().filterNotNull()
                } else {
                    emptyList()
                }

                if (uploadedImages.isEmpty() && selectedImageUris.isNotEmpty()) {
                    throw Exception("No se pudieron subir las imágenes")
                }

                Log.d("AddProductActivity", "Imágenes subidas: ${uploadedImages.size}")

                // PASO 2: Crear producto con las imágenes
                val productRequest = CreateProductRequest(
                    name = etName.text.toString().trim(),
                    team = etTeam.text.toString().trim(),
                    description = etDescription.text.toString().trim(),
                    price = etPrice.text.toString().toInt(),
                    stock = etStock.text.toString().toInt(),
                    image_urls = uploadedImages
                )

                val response = apiService.createProduct(productRequest)

                if (response.isSuccessful) {
                    Toast.makeText(this@AddProductActivity, "✅ Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                    clearForm()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = "Error al crear producto: ${response.code()}"
                    Log.e("AddProductActivity", "$errorMsg - $errorBody")
                    Toast.makeText(this@AddProductActivity, errorMsg, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("AddProductActivity", "Error: ${e.message}", e)
                Toast.makeText(this@AddProductActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setFormEnabled(true)
                // Si tienes progressBar, descoméntalo: progressBar.visibility = View.GONE
            }
        }
    }

    private fun setFormEnabled(enabled: Boolean) {
        etName.isEnabled = enabled
        etTeam.isEnabled = enabled
        etDescription.isEnabled = enabled
        etPrice.isEnabled = enabled
        etStock.isEnabled = enabled
        btnSelectImage.isEnabled = enabled
        btnSaveProduct.isEnabled = enabled

        // Cambiar apariencia visual
        if (enabled) {
            btnSaveProduct.alpha = 1.0f
            btnSelectImage.alpha = 1.0f
            btnSaveProduct.text = "Guardar producto"
        } else {
            btnSaveProduct.alpha = 0.6f
            btnSelectImage.alpha = 0.6f
            btnSaveProduct.text = "Creando..."
        }
    }

    private suspend fun uploadImage(uri: Uri): UploadImageResponse? {
        return try {
            // Convertir Uri a File
            val file = uriToFile(uri)
            val requestFile = file.asRequestBody(getMimeType(uri)?.toMediaTypeOrNull())

            // Crear Multipart Part - IMPORTANTE: el campo debe llamarse "content"
            val part = MultipartBody.Part.createFormData("content", file.name, requestFile)

            // Llamar al endpoint de upload
            val response = apiService.cargarImagen(part)

            if (response.isSuccessful) {
                response.body().also {
                    Log.d("AddProductActivity", "Imagen subida: ${it?.name}")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AddProductActivity", "Error al subir imagen: ${response.code()} - $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("AddProductActivity", "Error subiendo imagen $uri: ${e.message}", e)
            null
        }
    }

    private fun uriToFile(uri: Uri): File {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File.createTempFile("upload_", ".jpg", cacheDir)
            FileOutputStream(file).use { output ->
                inputStream?.copyTo(output)
            }
            file
        } catch (e: Exception) {
            throw IOException("Error converting URI to file", e)
        }
    }

    private fun getMimeType(uri: Uri): String? {
        return contentResolver.getType(uri) ?: "image/*"
    }

    private fun clearForm() {
        etName.text?.clear()
        etTeam.text?.clear()
        etDescription.text?.clear()
        etPrice.text?.clear()
        etStock.text?.clear()
        selectedImageUris.clear()
    }
}