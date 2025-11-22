package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName

data class AddToCartRequest(
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int
)
