package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    val id: Int,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("shirt_id")
    val shirtId: Int,
    val quantity: Int
)
