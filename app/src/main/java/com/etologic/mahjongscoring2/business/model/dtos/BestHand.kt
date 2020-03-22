package com.etologic.mahjongscoring2.business.model.dtos

import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE

class BestHand {
    
    var playerInitialPosition: TableWinds = NONE
    var playerName: String = ""
    var handValue: Int = 0
    
}
