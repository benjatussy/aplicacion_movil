package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    val name: String,
    val email: String,
    @SerializedName("role") val role: String,
    val status: String
)
