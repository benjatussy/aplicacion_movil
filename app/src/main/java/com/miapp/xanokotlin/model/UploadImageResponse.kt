package com.miapp.xanokotlin.model

import com.miapp.xanokotlin.model.Meta

data class UploadImageResponse(
    val access: String,
    val path: String,
    val name: String,
    val type: String,
    val size: Long,
    val mime: String,
    val meta: Meta
)
