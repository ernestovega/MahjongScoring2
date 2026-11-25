package com.etologic.mahjongscoring2.app.utils

import com.etologic.mahjongscoring2.app.utils.DateTimeUtils.prettifyOneLine
import com.etologic.mahjongscoring2.app.utils.DateTimeUtils.prettifyTwoLines
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Date

class AppUtilsTest {

    @Test
    fun `signed numbers`() {
        assertThat(5.toStringSigned()).isEqualTo("+5")
        assertThat(0.toStringSigned()).isEqualTo("0")
        assertThat((-5).toStringSigned()).isEqualTo("-5")
    }

    @Test
    fun `nullable int to string or hyphen`() {
        assertThat(null.toStringOrHyphen()).isEqualTo("-")
        assertThat(0.toStringOrHyphen()).isEqualTo("0")
        assertThat((-3).toStringOrHyphen()).isEqualTo("-3")
    }

    @Test
    fun `capitalize first letter only`() {
        assertThat("hello world".capitalize()).isEqualTo("Hello world")
        assertThat("Hello".capitalize()).isEqualTo("Hello")
        assertThat("".capitalize()).isEqualTo("")
        assertThat("1abc".capitalize()).isEqualTo("1abc")
    }

    @Test
    fun `positional list accessors`() {
        val list = listOf(1, 2, 3, 4)
        assertThat(list.second()).isEqualTo(2)
        assertThat(list.third()).isEqualTo(3)
        assertThat(list.fourth()).isEqualTo(4)
        assertThrows<NoSuchElementException> { listOf(1).second() }
        assertThrows<NoSuchElementException> { listOf(1, 2).third() }
        assertThrows<NoSuchElementException> { listOf(1, 2, 3).fourth() }
    }

    @Test
    fun `pretty dates`() {
        val date = Date(1_736_935_200_000L) // 2025-01-15T10:00:00Z
        assertThat(date.prettifyOneLine()).isEqualTo("15/01/2025 10:00")
        assertThat(date.prettifyTwoLines()).isEqualTo("15/01/2025\n10:00")
    }

    @Test
    fun `date equality is by reference`() {
        val date = Date(1)
        assertThat(DateTimeUtils.areEqual(null, null)).isTrue()
        assertThat(DateTimeUtils.areEqual(date, null)).isFalse()
        assertThat(DateTimeUtils.areEqual(null, date)).isFalse()
        assertThat(DateTimeUtils.areEqual(date, date)).isTrue()
        // Same instant but different instances: considered different (current behaviour).
        assertThat(DateTimeUtils.areEqual(date, Date(1))).isFalse()
    }
}
