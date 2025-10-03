package com.example.aihighpulse.server.config

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Env {
    private val cache: Map<String, String> by lazy { load() }

    operator fun get(key: String): String? = System.getenv(key) ?: cache[key]

    private fun load(): Map<String, String> {
        val cwd = Paths.get("").toAbsolutePath().normalize()
        val map = mutableMapOf<String, String>()
        val candidates = listOf(
            cwd.resolve(".env.local"),
            cwd.resolve(".env"),
            cwd.parent?.resolve(".env.local"),
            cwd.parent?.resolve(".env"),
            cwd.resolve("server/.env.local"),
            cwd.resolve("server/.env"),
            cwd.parent?.resolve("server/.env.local"),
            cwd.parent?.resolve("server/.env")
        ).filterNotNull()
        candidates.forEach { readFileIfExists(it, map) }
        if (map.isEmpty()) {
            println("[Env] No env files found (cwd=$cwd)")
        } else {
            println("[Env] Loaded keys: ${map.keys}")
        }
        return map
    }

    private fun readFileIfExists(path: Path, target: MutableMap<String, String>) {
        if (!Files.exists(path)) return
        readFile(path, target)
    }

    private fun readFile(path: Path, target: MutableMap<String, String>) {
        runCatching {
            Files.newBufferedReader(path).use { reader ->
                reader.lineSequence()
                    .map { line -> line.trim().removePrefix("\uFEFF") }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .forEach { cleaned ->
                        val idx = cleaned.indexOf('=')
                        if (idx > 0) {
                            val key = cleaned.substring(0, idx).trim().removePrefix("\uFEFF")
                            val value = cleaned.substring(idx + 1).trim().trim('"')
                            if (key.isNotEmpty() && key !in target) {
                                target[key] = value
                            }
                        }
                    }
            }
            println("[Env] Loaded $path")
        }.onFailure {
            System.err.println("[Env] Failed to read $path: ${it.message}")
        }
    }
}
