package com.dang1000.releaspoon.network

import Artifacts
import kotlinx.serialization.json.Json
import retrofit2.HttpException

data class ApiErrorDto(val detail: String? = null, val message: String? = null)

class ChangelogRepository(private val api: ChangelogApi) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetchArtifacts(fileUrl: String, fileTypeHint: String): Result<Artifacts> {
        return try {
            require(fileTypeHint.isNotBlank()) { "file_type_hint is required." }
            val res = api.summarizeDeps(AnalyzeRequest(fileUrl, fileTypeHint))
            Result.success(res)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            val parsed = runCatching { json.decodeFromString<ApiErrorDto>(body ?: "") }.getOrNull()
            val msg = parsed?.detail ?: parsed?.message ?: "HTTP ${e.code()} ${e.message()}"
            Result.failure(IllegalStateException(msg, e))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
