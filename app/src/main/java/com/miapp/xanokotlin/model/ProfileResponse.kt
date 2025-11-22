package com.miapp.xanokotlin.model

data class ProfileResponse(
    val id: Int,
    val created_at: Long,
    val name: String,
    val email: String
)
