package es.etologic.mahjongscoring2.domain.model

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE

class BestHand {
    
    var playerInitialPosition: TableWinds = NONE
    var playerName: String = ""
    var handValue: Int = 0
    
}
