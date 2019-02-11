package es.etologic.mahjongscoring2.app.model;

import es.etologic.mahjongscoring2.domain.model.Game;
import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

import static es.etologic.mahjongscoring2.domain.model.enums.TableWinds.NONE;

public class SelectedPlayerSeat {

    private TableWinds currentSeat;
    private TableWinds initialSeat;

    public TableWinds getCurrentSeat() {
        return currentSeat;
    }
    public TableWinds getInitialSeat() {
        return initialSeat;
    }

    public SelectedPlayerSeat() {
        this.currentSeat = NONE;
        this.initialSeat = NONE;
    }

    public void clear() {
        this.currentSeat = NONE;
        this.initialSeat = NONE;
    }

    public void setSelectedPlayer(TableWinds currentSeat, int roundId) {
        if(currentSeat == null || currentSeat == NONE) {
            clear();
        } else {
            this.currentSeat = currentSeat;
            this.initialSeat = Game.getPlayerInitialPositionByCurrentSeat(currentSeat, roundId);
        }
    }
}
