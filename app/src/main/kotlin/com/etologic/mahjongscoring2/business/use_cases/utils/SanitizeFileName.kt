package com.etologic.mahjongscoring2.business.use_cases.utils

import java.text.Normalizer

/**
 * Sanitizes the string to be safe for use as a filename across Windows, macOS, Linux, and Android.
 *
 * This function normalizes the string using Unicode NFC normalization, replaces characters invalid
 * in filenames with the specified replacement string, trims trailing dots and spaces, prevents
 * reserved Windows filenames, preserves file extensions, truncates to a maximum length if necessary,
 * and falls back to a default name if the result is empty.
 *
 * @param replacement The string to replace invalid filename characters with. Defaults to "_".
 * @param maxComponentLength The maximum allowed length of the sanitized filename including extension. Defaults to 255.
 * @return A sanitized filename string safe for use on multiple platforms.
 */
fun String.sanitizeForFileName(
    replacement: String = "_",
    maxComponentLength: Int = 255
): String {
    // Unicode normalization and trimming
    val name = Normalizer.normalize(this, Normalizer.Form.NFC).trim()

    // Splitting base and extension
    val lastDot = name.lastIndexOf('.')
    val hasExtension = lastDot > 0 && lastDot < name.length - 1
    val rawBase = if (hasExtension) name.substring(0, lastDot) else name
    val rawExt  = if (hasExtension) name.substring(lastDot + 1) else ""

    // Replacing invalid characters
    val invalidPattern = Regex("""[\\/:*?"<>|\p{Cntrl}\p{Cf}]""")
    fun replaceInvalid(s: String): String = s.replace(invalidPattern, replacement)

    var base = replaceInvalid(rawBase)
    var ext  = replaceInvalid(rawExt)

    // Collapsing multiple replacements
    fun collapseRuns(s: String): String {
        val run = Regex(Regex.escape(replacement) + "{2,}")
        return s.replace(run, replacement)
    }
    base = collapseRuns(base)
    ext = collapseRuns(ext)

    // Trimming trailing dots/spaces
    fun trimTrailingDotsSpaces(s: String): String {
        var t = s
        while (t.isNotEmpty() && (t.last() == ' ' || t.last() == '.')) t = t.dropLast(1)
        return t
    }
    base = trimTrailingDotsSpaces(base)

    // Handling empty base and reserved names
    if (base.isEmpty()) base = "file"
    if (base.uppercase() in RESERVED_WINDOWS) base = "_" + base

    // Recombining with extension
    fun compose(b: String, e: String): String = if (e.isEmpty()) b else "$b.$e"
    var sanitized = compose(base, ext)

    // Enforcing length limit
    if (sanitized.length > maxComponentLength) {
        val extPart = if (ext.isEmpty()) "" else ".$ext"
        val allowedBaseLen = (maxComponentLength - extPart.length).coerceAtLeast(1)
        val truncatedBase = base.take(allowedBaseLen)
        sanitized = compose(truncatedBase, ext)
    }

    // Final non-empty fallback
    if (sanitized.isEmpty()) sanitized = "file"

    return sanitized
}

private val RESERVED_WINDOWS = setOf(
    "CON","PRN","AUX","NUL",
    "COM1","COM2","COM3","COM4","COM5","COM6","COM7","COM8","COM9",
    "LPT1","LPT2","LPT3","LPT4","LPT5","LPT6","LPT7","LPT8","LPT9"
)
