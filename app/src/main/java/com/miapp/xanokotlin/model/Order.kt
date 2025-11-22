package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("user_name") val userName: String,
    val total: Double,
    val status: String
)
