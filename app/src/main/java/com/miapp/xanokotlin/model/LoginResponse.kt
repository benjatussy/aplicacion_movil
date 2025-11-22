package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("authToken")
    val authToken: String,

    @SerializedName("user")
    val user: User
)
