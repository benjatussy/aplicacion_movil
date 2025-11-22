package com.miapp.xanokotlin.Response

import java.io.Serializable

data class User_Response(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val status: String
): Serializable