package com.dang1000.releaspoon.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyzeRequest(
    @SerialName("file_url") val fileUrl: String,
    @SerialName("file_type_hint") val fileTypeHint: String
)