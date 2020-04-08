package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE

open class PenaltyData(
    internal val points: Int,
    internal val isDivided: Boolean
) {
    
    internal var penalizedPlayerCurrentSeat: TableWinds = NONE
}