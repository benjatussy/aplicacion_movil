package com.miapp.xanokotlin.model

import com.miapp.xanokotlin.model.UploadImageResponse

data class CreateProductRequest(
    val name: String,
    val team: String,
    val price: Int,
    val stock: Int,
    val description: String,
    val image_urls: List<UploadImageResponse>
)
