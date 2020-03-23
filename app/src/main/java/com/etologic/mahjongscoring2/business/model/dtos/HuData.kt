package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE

open class HuData(
    internal val winnerCurrentSeat: TableWinds,
    internal var discarderCurrentSeat: TableWinds? = NONE,
    internal val points: Int
) {
    
    constructor(winnerCurrentSeat: TableWinds, points: Int) : this(winnerCurrentSeat, null, points)
}