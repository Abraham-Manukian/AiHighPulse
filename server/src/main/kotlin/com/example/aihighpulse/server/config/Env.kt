package com.example.aihighpulse.server.config

import java.io.BufferedReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Env {
    private val cache: Map<String, String> by lazy { load() }

    operator fun get(key: String): String? = System.getenv(key) ?: cache[key]

    private fun load(): Map<String, String> {
        val paths = listOf(
            "server/.env.local",
            ".env.local",
            "server/.env",
            ".env"
        )
        val map = mutableMapOf<String, String>()
        for (raw in paths) {
            val path = Paths.get(raw)
            if (Files.exists(path)) {
                readFile(path, map)
            }
        }
        return map
    }

    private fun readFile(path: Path, target: MutableMap<String, String>) {
        runCatching {
            Files.newBufferedReader(path).use { reader ->
                reader.lineSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .forEach { line ->
                        val idx = line.indexOf('=')
                        if (idx > 0) {
                            val key = line.substring(0, idx).trim()
                            val value = line.substring(idx + 1).trim().trim('"')
                            if (key.isNotEmpty() && key !in target) {
                                target[key] = value
                            }
                        }
                    }
            }
        }.onFailure {
            System.err.println("[Env] Failed to read ${'$'}path: ${'$'}{it.message}")
        }
    }
}
