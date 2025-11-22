package com.miapp.xanokotlin.Response

data class ProfileResponse(
    val id: Int,
    val created_at: Long,
    val name: String,
    val email: String
)