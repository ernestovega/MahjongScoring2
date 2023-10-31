package com.etologic.mahjongscoring2.app.model

import com.etologic.mahjongscoring2.business.model.enums.TableWinds

data class Seat(
    val name: String,
    val seatWind: TableWinds,
)
