package com.miapp.xanokotlin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    val id: Int,
    val name: String,
    val email: String,

    @SerializedName("role") // <-- ESTA ES LA CORRECCIÓN DEFINITIVA
    val rol: String?,
    
    val status: String
) : Serializable
