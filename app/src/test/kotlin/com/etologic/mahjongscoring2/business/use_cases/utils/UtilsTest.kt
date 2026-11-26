package com.etologic.mahjongscoring2.business.use_cases.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File
import java.util.Date

class UtilsTest {

    @ParameterizedTest
    @CsvSource(
        delimiter = '|',
        value = [
            "Ana|Ana",
            "'  Ana  '|Ana",
            "'\"Ana\"'|Ana",
            "'An\"a'|Ana",
            "''|''",
            "'   '|''",
            "José Luis|José Luis",
        ],
    )
    fun `normalize name trims and removes double quotes`(input: String, expected: String) {
        assertThat(normalizeName(input)).isEqualTo(expected)
    }

    @Test
    fun `normalize null name`() {
        assertThat(normalizeName(null)).isEmpty()
    }

    @Test
    fun `date formats use the default locale and time zone`() {
        val date = Date(1_736_935_200_000L) // 2025-01-15T10:00:00Z, tests run in UTC
        assertThat(date.toGameNameFormat()).isEqualTo("15/01/2025 10:00")
        assertThat(date.toFileNameFormat()).isEqualTo("2025_01_15_10_00")
    }

    @Test
    fun `write to file sanitizes name and appends extension`(@TempDir dir: File) {
        val file = writeToFile("a/b:c", "csv", "content", dir)
        assertThat(file.name).isEqualTo("a_b_c.csv")
        assertThat(file.parentFile).isEqualTo(dir)
        assertThat(file.readText()).isEqualTo("content")
    }

    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource(
        delimiter = '|',
        value = [
            "game|game",
            "'  game  '|game",
            "my game.csv|my game.csv",
            "a/b\\c:d*e?f\"g<h>i|a_b_c_d_e_f_g_h_i",
            "a//b|a_b",
            "'game. . .'|game",
            "'...'|file",
            "''|file",
            "CON|_CON",
            "con.txt|_con.txt",
            "LPT9|_LPT9",
            "COM10|COM10",
            ".hidden|.hidden",
            "name.|name",
            "archive.tar.gz|archive.tar.gz",
            "Torneo Primavera - Mesa 3|Torneo Primavera - Mesa 3",
            "王伟 🀄|王伟 🀄",
        ],
    )
    fun `sanitize for file name`(input: String, expected: String) {
        assertThat(input.sanitizeForFileName()).isEqualTo(expected)
    }

    @Test
    fun `sanitize removes control characters and supports custom replacement`() {
        assertThat("a\tb\nc\u0000d​e".sanitizeForFileName()).isEqualTo("a_b_c_d_e")
        assertThat("a/b".sanitizeForFileName(replacement = "-")).isEqualTo("a-b")
    }

    @Test
    fun `sanitize normalizes unicode to NFC`() {
        val decomposed = "José"
        assertThat(decomposed.sanitizeForFileName()).isEqualTo("José")
    }

    @Test
    fun `sanitize truncates keeping the extension`() {
        val long = "x".repeat(300) + ".json"
        val result = long.sanitizeForFileName()
        assertThat(result).hasLength(255)
        assertThat(result).endsWith(".json")
        assertThat("abcdef.json".sanitizeForFileName(maxComponentLength = 8)).isEqualTo("abc.json")
        assertThat("abcdef".sanitizeForFileName(maxComponentLength = 3)).isEqualTo("abc")
    }
}
