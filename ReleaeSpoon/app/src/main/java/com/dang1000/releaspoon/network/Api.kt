package com.dang1000.releaspoon.network

import Artifacts
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.MediaType.Companion.toMediaType

interface ChangelogApi {
    @POST("/summarize-deps")
    suspend fun summarizeDeps(@Body body: AnalyzeRequest): Artifacts
}

object ApiProvider {
    fun create(baseUrl: String): ChangelogApi {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
        val contentType = "application/json".toMediaType()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()

        return retrofit.create(ChangelogApi::class.java)
    }
}
