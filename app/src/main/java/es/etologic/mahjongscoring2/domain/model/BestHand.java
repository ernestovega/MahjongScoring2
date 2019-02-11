package es.etologic.mahjongscoring2.domain.model;

import es.etologic.mahjongscoring2.domain.model.enums.TableWinds;

public class BestHand {

    private TableWinds playerInitialPosition;
    private String playerName;
    private int handValue;

    TableWinds getPlayerInitialPosition() {
        return playerInitialPosition;
    }
    public String getPlayerName() {
        return playerName;
    }
    public int getHandValue() {
        return handValue;
    }

    void setPlayerInitialPosition(TableWinds playerInitialPosition) {
        this.playerInitialPosition = playerInitialPosition;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }

    BestHand() {
        playerName = "";
        handValue = 0;
    }
}
