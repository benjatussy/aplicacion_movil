package com.miapp.xanokotlin.api

import com.miapp.xanokotlin.model.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    // --- AUTH --- //
    @POST("auth/signup")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<RegistroResponse>

    @POST("auth/login")
    suspend fun loginUsuario(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    // --- USERS --- //
    @GET("user")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<User>>

    @PUT("user/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") userId: Int, @Body request: UserRequest): Response<User>

    @DELETE("user/{user_id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("user_id") userId: Int): Response<Unit>

    // --- PRODUCTS --- //
    @GET("shirt")
    suspend fun getProducts(@Header("Authorization") token: String): Response<List<Product>>

    @GET("shirt/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>

    @POST("shirt")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<Product>

    @PUT("shirt/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: Product): Response<Product>

    @DELETE("shirt/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>

    // --- IMAGE UPLOAD --- //
    @Multipart
    @POST("upload/image")
    suspend fun cargarImagen(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    // --- ORDERS --- //
    @GET("orders")
    suspend fun getOrders(@Header("Authorization") token: String): Response<List<Order>>

    @POST("order/{id}/accept")
    suspend fun acceptOrder(@Header("Authorization") token: String, @Path("id") orderId: Int): Response<Order>

    @POST("order/{id}/reject")
    suspend fun rejectOrder(@Header("Authorization") token: String, @Path("id") orderId: Int): Response<Order>

    // --- CART --- //
    @GET("cart")
    suspend fun getCartItems(@Header("Authorization") token: String): Response<List<CartItem>>

    @POST("cart")
    suspend fun addToCart(@Header("Authorization") token: String, @Body request: AddToCartRequest): Response<Unit>


    companion object {
        private const val BASE_URL_AUTH = "https://x8ki-letl-twmt.n7.xano.io/api:oLaqHDUK/"
        private const val BASE_URL_PRODUCTS = "https://x8ki-letl-twmt.n7.xano.io/api:yING-kjI/"

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        fun createAuthService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_AUTH)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun createProductService(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_PRODUCTS)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}