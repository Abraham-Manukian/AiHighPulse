package com.example.aihighpulse.shared.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val DEFAULT_REQUEST_TIMEOUT_MS = 30_000L
private const val DEFAULT_SOCKET_TIMEOUT_MS = 30_000L
private const val DEFAULT_CONNECT_TIMEOUT_MS = 15_000L

class ApiClient(val httpClient: HttpClient, val baseUrl: String) {
    suspend inline fun <reified Req : Any, reified Res : Any> post(path: String, body: Req): Res =
        httpClient.post("$baseUrl$path") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend inline fun <reified Res : Any> get(path: String): Res =
        httpClient.get("$baseUrl$path").body()
}

fun createHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = DEFAULT_REQUEST_TIMEOUT_MS
        socketTimeoutMillis = DEFAULT_SOCKET_TIMEOUT_MS
        connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MS
    }
    install(Logging) {
        level = LogLevel.INFO
    }
}

@Serializable
data class SignupRequest(val email: String, val password: String)
@Serializable
data class SignupResponse(val userId: String, val token: String)