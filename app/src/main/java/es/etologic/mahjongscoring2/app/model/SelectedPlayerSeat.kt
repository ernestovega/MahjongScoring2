package es.etologic.mahjongscoring2.app.model

import es.etologic.mahjongscoring2.domain.model.GameRounds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE

class SelectedPlayerSeat(var currentSeat: TableWinds = NONE, var initialSeat: TableWinds = NONE) {
    
    fun setSelectedPlayer(currentSeat: TableWinds?, roundId: Int) {
        if (currentSeat == null || currentSeat === NONE) {
            clear()
        } else {
            this.currentSeat = currentSeat
            this.initialSeat = GameRounds.getPlayerInitialSeatByCurrentSeat(currentSeat, roundId)
        }
    }
    
    fun clear() {
        this.currentSeat = NONE
        this.initialSeat = NONE
    }
}
