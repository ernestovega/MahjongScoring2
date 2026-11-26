package com.etologic.mahjongscoring2.testutil

import java.io.File

/**
 * Text golden files ("approval tests").
 *
 * Every golden lives under `app/src/test/resources/golden/` and is plain UTF-8 text, so the very same
 * files can be reused verbatim by a future Kotlin Multiplatform `commonTest` to prove the port behaves
 * identically.
 *
 * - Normal run: the produced text must be byte-identical to the stored golden.
 * - `./gradlew testDebugUnitTest -Pupdate.goldens=true`: (re)writes the goldens instead of comparing.
 * - A missing golden is written on first run and the test fails, so it is never silently accepted.
 */
object Goldens {

    private val updateGoldens: Boolean
        get() = System.getProperty("update.goldens")?.toBoolean() == true

    val rootDir: File
        get() = File(
            System.getProperty("goldens.dir")
                ?: error("System property 'goldens.dir' not set. Run the tests through Gradle.")
        )

    fun file(relativePath: String): File = File(rootDir, relativePath)

    fun read(relativePath: String): String = file(relativePath).readText()

    fun assertMatches(relativePath: String, actual: String) {
        val goldenFile = file(relativePath)
        val normalizedActual = actual.replace("\r\n", "\n")
        if (updateGoldens) {
            goldenFile.parentFile.mkdirs()
            goldenFile.writeText(normalizedActual)
            return
        }
        if (!goldenFile.exists()) {
            goldenFile.parentFile.mkdirs()
            goldenFile.writeText(normalizedActual)
            throw AssertionError(
                "Golden file did not exist and has been created: ${goldenFile.path}. " +
                        "Review it, commit it and run the test again."
            )
        }
        val expected = goldenFile.readText().replace("\r\n", "\n")
        if (expected != normalizedActual) {
            throw AssertionError(buildDiffMessage(goldenFile, expected, normalizedActual))
        }
    }

    private fun buildDiffMessage(goldenFile: File, expected: String, actual: String): String {
        val expectedLines = expected.lines()
        val actualLines = actual.lines()
        val firstDifferentLine = (0 until maxOf(expectedLines.size, actualLines.size))
            .first { expectedLines.getOrNull(it) != actualLines.getOrNull(it) }
        return buildString {
            appendLine("Output differs from golden file ${goldenFile.path}")
            appendLine("First difference at line ${firstDifferentLine + 1}:")
            appendLine("  expected: ${expectedLines.getOrNull(firstDifferentLine)}")
            appendLine("  actual:   ${actualLines.getOrNull(firstDifferentLine)}")
            appendLine("Run with -Pupdate.goldens=true only if the change is intended.")
        }
    }
}
