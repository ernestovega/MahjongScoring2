package com.etologic.mahjongscoring2.business.model.enums

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** The numeric codes are persisted (Room, DataStore, JSON exports): they must never change. */
class EnumsTest {

    @Test
    fun `table winds codes`() {
        assertThat(TableWinds.entries.map { it.name to it.code }).containsExactly(
            "NONE" to -1, "EAST" to 0, "SOUTH" to 1, "WEST" to 2, "NORTH" to 3,
        ).inOrder()
        assertThat(TableWinds.asArray.toList()).containsExactly(
            TableWinds.EAST, TableWinds.SOUTH, TableWinds.WEST, TableWinds.NORTH,
        ).inOrder()
    }

    @Test
    fun `table winds from code`() {
        assertThat(TableWinds.from(null)).isNull()
        assertThat(TableWinds.from(-1)).isEqualTo(TableWinds.NONE)
        assertThat(TableWinds.from(0)).isEqualTo(TableWinds.EAST)
        assertThat(TableWinds.from(1)).isEqualTo(TableWinds.SOUTH)
        assertThat(TableWinds.from(2)).isEqualTo(TableWinds.WEST)
        assertThat(TableWinds.from(3)).isEqualTo(TableWinds.NORTH)
        assertThat(TableWinds.from(4)).isEqualTo(TableWinds.NONE)
        assertThat(TableWinds.from(-99)).isEqualTo(TableWinds.NONE)
    }

    @Test
    fun `seats arrangement codes and fallback`() {
        assertThat(SeatsArrangement.entries.map { it.name to it.code }).containsExactly(
            "DOWN" to 0, "OUT" to 1, "RANKING" to 2,
        ).inOrder()
        assertThat(SeatsArrangement.from(0)).isEqualTo(SeatsArrangement.DOWN)
        assertThat(SeatsArrangement.from(1)).isEqualTo(SeatsArrangement.OUT)
        assertThat(SeatsArrangement.from(2)).isEqualTo(SeatsArrangement.RANKING)
        assertThat(SeatsArrangement.from(null)).isEqualTo(SeatsArrangement.DOWN)
        assertThat(SeatsArrangement.from(7)).isEqualTo(SeatsArrangement.DOWN)
    }

    @Test
    fun `share options by dialog index`() {
        assertThat(ShareGameOptions.fromIndex(0)).isEqualTo(ShareGameOptions.TEXT)
        assertThat(ShareGameOptions.fromIndex(1)).isEqualTo(ShareGameOptions.CSV)
        assertThat(ShareGameOptions.fromIndex(2)).isEqualTo(ShareGameOptions.JSON)
        assertThrows<IllegalArgumentException> { ShareGameOptions.fromIndex(3) }
        assertThrows<IllegalArgumentException> { ShareGameOptions.fromIndex(-1) }
    }
}
