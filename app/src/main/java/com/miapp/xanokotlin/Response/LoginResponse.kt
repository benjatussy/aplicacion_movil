package com.miapp.xanokotlin.Response

import com.google.gson.annotations.SerializedName
import com.miapp.xanokotlin.model.User

data class LoginResponse(
    @SerializedName("authToken")
    val authToken: String,

    @SerializedName("user")
    val user: User
)
