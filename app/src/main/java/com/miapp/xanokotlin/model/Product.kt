package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("image_urls") val imageUrls: List<ApiImage>?
)
