package com.example.aihighpulse.server.llm

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.serialization.SerializationException
import kotlin.collections.linkedSetOf
import org.slf4j.Logger

private const val DEFAULT_REPAIR_ATTEMPTS = 3
private const val RAW_SNIPPET_LIMIT = 160

/**
 * Retries LLM generation with structured feedback when the returned payload is invalid.
 */
object LlmRepairer {
    suspend fun <T> generate(
        llm: LLMClient,
        locale: Locale,
        operation: String,
        logger: Logger,
        requestId: String? = null,
        buildPrompt: (attempt: Int, feedback: String?) -> String,
        decode: (String) -> T,
        validate: (T) -> String?,
        textExtractor: (T) -> List<String>,
        maxAttempts: Int = DEFAULT_REPAIR_ATTEMPTS
    ): T {
        var feedback: String? = null
        var lastError: Throwable? = null
        var lastRaw: String? = null
        val appliedFixes = linkedSetOf<String>()

        for (attempt in 1..maxAttempts) {
            val prompt = buildPrompt(attempt, feedback)
            var currentRaw = llm.generateJson(prompt).trim()
            LlmRawStore.write(operation, requestId, attempt, "raw", currentRaw)
            lastRaw = currentRaw
            if (currentRaw.isEmpty()) {
                val error = IllegalStateException("LLM returned empty response")
                LlmErrorTracker.recordFailure(
                    logger = logger,
                    operation = operation,
                    requestId = requestId,
                    attempt = attempt,
                    stage = "response",
                    category = "empty_response",
                    message = error.message,
                    rawSnippet = null
                )
                feedback = "Previous response was empty. Return a non-empty JSON payload that matches the schema exactly."
                lastError = error
                continue
            }

            val decodeResult = attemptDecode(
                raw = currentRaw,
                decode = decode,
                logger = logger,
                operation = operation,
                attempt = attempt,
                requestId = requestId,
                appliedFixesCollector = appliedFixes
            )
            currentRaw = decodeResult.raw
            if (decodeResult.value == null) {
                val error = decodeResult.error ?: IllegalStateException("Failed to decode LLM output")
                val category = classifyParseError(error, decodeResult.raw)
                val stage = if (decodeResult.fixesApplied.isEmpty()) "decode_pre" else "decode_post_autofix"
                LlmErrorTracker.recordFailure(
                    logger = logger,
                    operation = operation,
                    requestId = requestId,
                    attempt = attempt,
                    stage = stage,
                    category = category,
                    message = error.message,
                    rawSnippet = truncateRaw(decodeResult.raw)
                )
                feedback = buildJsonErrorFeedback(error, decodeResult.raw)
                lastError = error
                lastRaw = decodeResult.raw
                continue
            }

            lastRaw = decodeResult.raw
            val parsed = decodeResult.value
            val validationIssue = validate(parsed)
            if (validationIssue != null) {
                val category = classifyValidationIssue(validationIssue)
                LlmErrorTracker.recordFailure(
                    logger = logger,
                    operation = operation,
                    requestId = requestId,
                    attempt = attempt,
                    stage = "validation",
                    category = category,
                    message = validationIssue,
                    rawSnippet = truncateRaw(lastRaw)
                )
                feedback = buildValidationFeedback(validationIssue, lastRaw)
                lastError = IllegalStateException(validationIssue)
                continue
            }

            val languageIssue = detectLanguageIssue(locale, textExtractor(parsed))
            if (languageIssue != null) {
                LlmErrorTracker.recordFailure(
                    logger = logger,
                    operation = operation,
                    requestId = requestId,
                    attempt = attempt,
                    stage = "language",
                    category = "language_mismatch",
                    message = languageIssue,
                    rawSnippet = truncateRaw(lastRaw)
                )
                feedback = languageIssue
                lastError = IllegalStateException(languageIssue)
                continue
            }

            LlmErrorTracker.recordSuccess(
                logger = logger,
                operation = operation,
                requestId = requestId,
                attemptsUsed = attempt,
                appliedFixes = appliedFixes
            )
            return parsed
        }

        val reason = lastError?.message ?: "unknown"
        val snippet = truncateRaw(lastRaw)
        LlmErrorTracker.recordFailure(
            logger = logger,
            operation = operation,
            requestId = requestId,
            attempt = maxAttempts,
            stage = "final",
            category = "final_failure",
            message = reason,
            rawSnippet = snippet
        )
        throw IllegalStateException(
            "LLM $operation failed after $maxAttempts attempts. Last error: $reason. Last raw snippet: ${snippet ?: "<empty>"}",
            lastError
        )
    }

    private data class DecodeResult<T>(
        val value: T?,
        val raw: String,
        val error: Throwable?,
        val fixesApplied: Set<String> = emptySet(),
        val fixSucceeded: Boolean = false
    )

    private data class AutoRepairResult(val content: String, val fixes: Set<String>)

    private fun <T> attemptDecode(
        raw: String,
        decode: (String) -> T,
        logger: Logger,
        operation: String,
        attempt: Int,
        requestId: String?,
        appliedFixesCollector: MutableSet<String>
    ): DecodeResult<T> {
        var currentRaw = raw
        val firstAttempt = runCatching { decode(currentRaw) }
        if (firstAttempt.isSuccess) {
            return DecodeResult(firstAttempt.getOrNull(), currentRaw, null)
        }
        val firstError = firstAttempt.exceptionOrNull()
        val recoveredFirst = trySafeRecovery(currentRaw, decode)
        if (recoveredFirst != null) {
            appliedFixesCollector += "fallback_truncate"
            LlmErrorTracker.recordAutoFix(
                logger,
                operation,
                requestId,
                fixes = setOf("fallback_truncate"),
                success = true,
                rawSnippet = truncateRaw(currentRaw)
            )
            return DecodeResult(recoveredFirst, currentRaw, null, setOf("fallback_truncate"), true)
        }

        val repaired = attemptAutoRepair(currentRaw)
        if (repaired != null) {
            appliedFixesCollector += repaired.fixes
            currentRaw = repaired.content
            LlmRawStore.write(operation, requestId, attempt, "autofix", currentRaw)
            val secondAttempt = runCatching { decode(currentRaw) }
            if (secondAttempt.isSuccess) {
                LlmErrorTracker.recordAutoFix(logger, operation, requestId, repaired.fixes, success = true, rawSnippet = truncateRaw(currentRaw))
                return DecodeResult(secondAttempt.getOrNull(), currentRaw, null, repaired.fixes, true)
            }
            val error = secondAttempt.exceptionOrNull() ?: firstError
            val recoveredSecond = trySafeRecovery(currentRaw, decode)
            if (recoveredSecond != null) {
                val fixesWithFallback = repaired.fixes + "fallback_truncate"
                LlmErrorTracker.recordAutoFix(logger, operation, requestId, fixesWithFallback, success = true, rawSnippet = truncateRaw(currentRaw))
                appliedFixesCollector += fixesWithFallback
                return DecodeResult(recoveredSecond, currentRaw, null, fixesWithFallback, true)
            }
            LlmErrorTracker.recordAutoFix(logger, operation, requestId, repaired.fixes, success = false, rawSnippet = truncateRaw(currentRaw))
            return DecodeResult(null, currentRaw, error, repaired.fixes, false)
        }

        val recoveredDirect = trySafeRecovery(currentRaw, decode)
        if (recoveredDirect != null) {
            appliedFixesCollector += "fallback_truncate"
            LlmErrorTracker.recordAutoFix(
                logger,
                operation,
                requestId,
                fixes = setOf("fallback_truncate"),
                success = true,
                rawSnippet = truncateRaw(currentRaw)
            )
            return DecodeResult(recoveredDirect, currentRaw, null, setOf("fallback_truncate"), true)
        }

        return DecodeResult(null, currentRaw, firstError)
    }

    private fun attemptAutoRepair(raw: String): AutoRepairResult? {
        var fixed = raw.trim()
        val fixes = linkedSetOf<String>()

        fun applyFix(label: String, transform: (String) -> String) {
            val updated = transform(fixed)
            if (updated != fixed) {
                fixed = updated
                fixes += label
            }
        }

        applyFix("strip_markdown") { it.replace("```json", "```") }
        applyFix("strip_markdown") { it.replace("```", "") }

        val firstBrace = fixed.indexOf('{')
        val lastBrace = fixed.lastIndexOf('}')
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            val candidate = fixed.substring(firstBrace, lastBrace + 1)
            if (candidate != fixed) {
                fixed = candidate
                fixes += "strip_prefix_suffix"
            }
        }

        val lastBraceIndex = fixed.lastIndexOf('}')
        if (lastBraceIndex >= 0 && lastBraceIndex < fixed.length - 1) {
            fixed = fixed.substring(0, lastBraceIndex + 1)
            fixes += "strip_trailing_text"
        }

        applyFix("replace_single_quotes_keys") {
            it.replace(Regex("(?<=\\{|,|\\s)'([A-Za-z0-9_]+)'\\s*:"), "\"$1\":")
        }
        applyFix("replace_single_quotes_values") {
            it.replace(Regex(":\\s*'([^']*)'")) { match -> ": \"${match.groupValues[1]}\"" }
        }

        applyFix("unescape_escaped_quotes") {
            if (it.contains("\\\"")) it.replace("\\\"", "\"") else it
        }

        val bareKeyRegex = Regex("(?<=\\{|,)\\s*([A-Za-z0-9_]+)\\s*:")
        applyFix("quote_keys") {
            bareKeyRegex.replace(it) { match ->
                val key = match.groupValues[1]
                match.value.replaceFirst(key, "\"$key\"")
            }
        }

        applyFix("insert_missing_commas_between_objects") {
            it.replace(Regex("\\}(\\s*)\\{")) { match ->
                "},${match.groupValues[1]}{"
            }
        }

        applyFix("fix_misnested_braces") { fixMisnestedBracesInArrays(it) }

        val trailingCommaRegex = Regex(",\\s*([}\\]])")
        fun removeTrailingCommas(input: String): Pair<String, Boolean> {
            var temp = input
            var modified = false
            while (true) {
                val replaced = trailingCommaRegex.replace(temp) { match ->
                    modified = true
                    match.groupValues[1]
                }
                if (replaced == temp) break
                temp = replaced
            }
            return temp to modified
        }

        removeTrailingCommas(fixed).also { (result, changed) ->
            fixed = result
            if (changed) fixes += "remove_trailing_commas"
        }

        val danglingRegex = Regex("\"[^\"]*\"\\s*:\\s*[^,}\\]]*$")
        val danglingMatch = danglingRegex.find(fixed)
        if (danglingMatch != null) {
            val startIndex = (danglingMatch.range.first - 1).coerceAtLeast(0)
            val removeFrom = if (fixed[startIndex] == ',') startIndex else danglingMatch.range.first
            val updated = fixed.substring(0, removeFrom)
            if (updated != fixed) {
                fixed = updated
                fixes += "trim_dangling_pair"
            }
        }

        val quoteCount = fixed.count { it == '"' }
        if (quoteCount % 2 != 0) {
            val lastQuoteIndex = fixed.lastIndexOf('"')
            if (lastQuoteIndex >= 0) {
                fixed = fixed.substring(0, lastQuoteIndex)
                fixes += "trim_unmatched_quote"
            }
        }

        val braceDiff = fixed.count { it == '{' } - fixed.count { it == '}' }
        when {
            braceDiff > 0 -> {
                fixed += "}".repeat(braceDiff)
                fixes += "balance_braces"
            }
            braceDiff < 0 -> {
                var diff = -braceDiff
                while (diff > 0 && fixed.isNotEmpty()) {
                    val idx = fixed.lastIndexOf('}')
                    if (idx < 0) break
                    fixed = fixed.removeRange(idx, idx + 1)
                    diff--
                }
                fixes += "balance_braces"
            }
        }

        val bracketDiff = fixed.count { it == '[' } - fixed.count { it == ']' }
        when {
            bracketDiff > 0 -> {
                fixed += "]".repeat(bracketDiff)
                fixes += "balance_brackets"
            }
            bracketDiff < 0 -> {
                var diff = -bracketDiff
                while (diff > 0 && fixed.isNotEmpty()) {
                    val idx = fixed.lastIndexOf(']')
                    if (idx < 0) break
                    fixed = fixed.removeRange(idx, idx + 1)
                    diff--
                }
                fixes += "balance_brackets"
            }
        }

        removeTrailingCommas(fixed).also { (result, changed) ->
            fixed = result
            if (changed) fixes += "remove_trailing_commas"
        }

        applyFix("add_default_meal_name") {
            it.replace(Regex("\\{\\s*\"ingredients\"")) { "{\"name\":\"Untitled Meal\",\"ingredients\"" }
        }

        applyFix("ensure_ingredients_array") {
            it.replace(Regex("\\{(\\s*\"name\"\\s*:\\s*\"[^\"]+\"\\s*,)(\\s*\"macros\")")) { match ->
                "{${match.groupValues[1]}\"ingredients\": [],${match.groupValues[2]}"
            }
        }

        applyFix("default_sleepAdvice") {
            it.replace(
                Regex("\"sleepAdvice\"\\s*:\\s*null"),
                "\"sleepAdvice\":{\"messages\":[\"Поддерживайте стабильный режим сна, ограничьте экраны перед отдыхом.\"],\"disclaimer\":\"Советы носят ознакомительный характер. При серьёзных нарушениях сна обратитесь к врачу.\"}"
            )
        }

        fixed = fixed.trim()
        return if (fixes.isNotEmpty()) AutoRepairResult(fixed, fixes) else null
    }

    private fun buildJsonErrorFeedback(error: Throwable, raw: String): String {
        val message = error.message?.lowercase().orEmpty()
        val snippet = truncateRaw(raw)
        val base = when {
            "trailing comma" in message -> "Previous response contained trailing commas. Remove every trailing comma from arrays and objects and resend valid JSON."
            "expected quotation mark" in message -> "Previous response had missing or mismatched quote characters. Ensure every key and string value is wrapped in double quotes and no quotes are duplicated."
            "unexpected eof" in message || "unexpected end of input" in message -> "Previous response ended abruptly. Return a complete JSON document with all closing brackets and braces."
            else -> "Previous response contained invalid JSON: ${error.message ?: "parse error"}. Return strictly valid JSON that matches the schema and includes the required fields."
        }
        return if (snippet != null) {
            "$base Here is the beginning of your previous JSON for reference: $snippet"
        } else {
            base
        }
    }

    private fun buildValidationFeedback(issue: String, raw: String?): String {
        val snippet = truncateRaw(raw)
        return if (snippet != null) {
            "Validation failed: $issue. Adjust the JSON accordingly and resend only corrected JSON. Previous snippet: $snippet"
        } else {
            "Validation failed: $issue. Return corrected JSON only."
        }
    }

    private fun detectLanguageIssue(locale: Locale, texts: List<String>): String? {
        val expectedScripts = expectedScripts(locale) ?: return null
        if (expectedScripts.isEmpty()) return null
        val joined = texts.filter { it.isNotBlank() }.joinToString(separator = " ")
        if (joined.isBlank()) return null
        val letters = joined.filter { it.isLetter() }
        if (letters.isEmpty()) return null
        val hasExpected = letters.any { ch -> expectedScripts.contains(Character.UnicodeScript.of(ch.code)) }
        if (hasExpected) return null
        val languageDisplay = locale.getDisplayLanguage(locale).ifBlank { locale.language }
        val tag = locale.toLanguageTag()
        val expectedDescription = describeScripts(expectedScripts)
        val sample = joined.take(80)
        return "Text must use $languageDisplay ($tag). Rewrite textual fields using $expectedDescription only. Current sample: $sample"
    }

    private fun classifyParseError(error: Throwable?, raw: String?): String {
        val message = error?.message?.lowercase().orEmpty()
        val trimmed = raw?.trim()
        return when {
            trimmed != null && trimmed.isNotEmpty() && !trimmed.startsWith("{") -> "extra_prefix_suffix"
            "trailing comma" in message -> "syntax_trailing_comma"
            "expected quotation" in message || "missing closing quote" in message -> "syntax_quotes"
            "missing field" in message -> "missing_field"
            "unexpected json token" in message || "unexpected token" in message -> "syntax_unexpected_token"
            "unexpected eof" in message || "unexpected end of input" in message -> "syntax_incomplete"
            else -> "syntax_other"
        }
    }

    private fun classifyValidationIssue(issue: String): String {
        val lower = issue.lowercase()
        return when {
            "trainingplan" in lower -> "training_validation"
            "sleepadvice" in lower -> "sleep_validation"
            "meal" in lower -> "nutrition_missing_meal"
            else -> "validation_other"
        }
    }

    private fun <T> trySafeRecovery(raw: String, decode: (String) -> T): T? {
        val marker = "}}}"
        val index = raw.lastIndexOf(marker)
        if (index <= 0) return null
        val safeJson = raw.substring(0, index) + "}]}"
        return runCatching { decode(safeJson) }.getOrNull()
    }

    private fun fixMisnestedBracesInArrays(json: String): String =
        json
            .replace(Regex("\\}\\}\\]"), "}]")
            .replace(Regex("\\}\\]\\}"), "}]")
            .replace(Regex("\\}\\}\\}\\]"), "}]")
            .replace(Regex("\\]\\}\\}"), "]}")

    private fun expectedScripts(locale: Locale): Set<Character.UnicodeScript>? = when (locale.language.lowercase()) {
        "ru", "uk", "be", "bg", "kk", "ky", "mn", "sr" -> setOf(Character.UnicodeScript.CYRILLIC)
        "zh" -> setOf(Character.UnicodeScript.HAN)
        "ja" -> setOf(
            Character.UnicodeScript.HIRAGANA,
            Character.UnicodeScript.KATAKANA,
            Character.UnicodeScript.HAN
        )
        "ko" -> setOf(Character.UnicodeScript.HANGUL)
        else -> null
    }

    private fun describeScripts(scripts: Set<Character.UnicodeScript>): String = scripts.joinToString(
        separator = ", ",
        transform = {
            when (it) {
                Character.UnicodeScript.CYRILLIC -> "Cyrillic characters"
                Character.UnicodeScript.HAN -> "Chinese Han characters"
                Character.UnicodeScript.HIRAGANA -> "Japanese Hiragana"
                Character.UnicodeScript.KATAKANA -> "Japanese Katakana"
                Character.UnicodeScript.HANGUL -> "Hangul characters"
                else -> it.name.lowercase().replace('_', ' ')
            }
        }
    )

    private fun truncateRaw(raw: String?): String? = raw
        ?.replace('\n', ' ')
        ?.replace('\r', ' ')
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.take(RAW_SNIPPET_LIMIT)

    private object LlmRawStore {
        private val baseDir: Path = Paths.get("server", "logs", "llm")

        init {
            runCatching { Files.createDirectories(baseDir) }
        }

        fun write(operation: String, requestId: String?, attempt: Int, stage: String, content: String) {
            if (content.isEmpty()) return
            val sanitizedId = sanitize(requestId ?: "unknown")
            val fileName = "${operation}_${sanitizedId}_attempt${attempt}_${stage}.json"
            val target = baseDir.resolve(fileName)
            runCatching {
                Files.write(
                    target,
                    content.toByteArray(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
                )
            }
        }

        private fun sanitize(value: String): String =
            value.replace(Regex("[^A-Za-z0-9._-]"), "_")
    }

    private object LlmErrorTracker {
        private val counters = ConcurrentHashMap<String, AtomicInteger>()

        fun recordFailure(
            logger: Logger,
            operation: String,
            requestId: String?,
            attempt: Int,
            stage: String,
            category: String,
            message: String?,
            rawSnippet: String?
        ) {
            increment("failure:$operation:$category")
            logger.warn(
                "LLM {} requestId={} attempt={} stage={} category={} message={} snippet={}",
                operation,
                requestId ?: "unknown",
                attempt,
                stage,
                category,
                message ?: "n/a",
                rawSnippet ?: "<none>"
            )
            logger.debug("LLM {} stats snapshot {}", operation, snapshot())
        }

        fun recordAutoFix(
            logger: Logger,
            operation: String,
            requestId: String?,
            fixes: Set<String>,
            success: Boolean,
            rawSnippet: String?
        ) {
            if (fixes.isEmpty()) return
            val status = if (success) "success" else "retry"
            fixes.forEach { fix ->
                increment("autofix:$operation:$fix:$status")
            }
            logger.info(
                "LLM {} requestId={} auto-fix(es) [{}] applied (status={}, snippet={})",
                operation,
                requestId ?: "unknown",
                fixes.joinToString(","),
                status,
                rawSnippet ?: "<none>"
            )
            logger.debug("LLM {} stats snapshot {}", operation, snapshot())
        }

        fun recordSuccess(
            logger: Logger,
            operation: String,
            requestId: String?,
            attemptsUsed: Int,
            appliedFixes: Set<String>
        ) {
            increment("success:$operation")
            if (appliedFixes.isNotEmpty()) {
                increment("success:$operation:auto_fixed")
            } else {
                increment("success:$operation:clean")
            }
            logger.info(
                "LLM {} requestId={} succeeded after {} attempt(s){}.",
                operation,
                requestId ?: "unknown",
                attemptsUsed,
                if (appliedFixes.isNotEmpty()) " (auto-fixes: ${appliedFixes.joinToString(",")})" else ""
            )
            logger.debug("LLM {} stats snapshot {}", operation, snapshot())
        }

        private fun increment(key: String) {
            counters.computeIfAbsent(key) { AtomicInteger(0) }.incrementAndGet()
        }

        fun snapshot(): Map<String, Int> =
            counters.mapValues { it.value.get() }
    }
}
