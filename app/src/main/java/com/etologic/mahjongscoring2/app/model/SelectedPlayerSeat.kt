package com.etologic.mahjongscoring2.app.model

import com.etologic.mahjongscoring2.app.utils.GameRoundsUtils
import com.etologic.mahjongscoring2.business.model.enums.TableWinds
import com.etologic.mahjongscoring2.business.model.enums.TableWinds.NONE

class SelectedPlayerSeat(
    var currentSeat: TableWinds = NONE,
    var initialSeat: TableWinds = NONE
) {
    
    fun setSelectedPlayer(currentSeat: TableWinds?, roundId: Int) {
        if (currentSeat == null || currentSeat === NONE) {
            clear()
        } else {
            this.currentSeat = currentSeat
            this.initialSeat = GameRoundsUtils.getPlayerInitialSeatByCurrentSeat(currentSeat, roundId)
        }
    }
    
    fun clear() {
        this.currentSeat = NONE
        this.initialSeat = NONE
    }
}
